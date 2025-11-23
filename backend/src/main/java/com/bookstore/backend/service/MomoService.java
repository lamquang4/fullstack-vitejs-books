package com.bookstore.backend.service;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.bookstore.backend.dto.MomoRequest;
import com.bookstore.backend.dto.MomoResponse;
import com.bookstore.backend.dto.OrderDTO;
import com.bookstore.backend.entities.Book;
import com.bookstore.backend.entities.Cart;
import com.bookstore.backend.entities.Order;
import com.bookstore.backend.entities.OrderDetail;
import com.bookstore.backend.entities.Payment;
import com.bookstore.backend.repository.BookRepository;
import com.bookstore.backend.repository.CartRepository;
import com.bookstore.backend.repository.OrderRepository;
import com.bookstore.backend.repository.PaymentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
@Service
public class MomoService {

    @Value("${momo.partnercode}")
    private String partnerCode;

    @Value("${momo.accesskey}")
    private String accessKey;

    @Value("${momo.secretkey}")
    private String secretKey;

    @Value("${momo.url}")
    private String momoUrl;

    @Value("${momo.redirect-url}")
    private String redirectUrl;

    @Value("${momo.ipn-url}")
    private String ipnUrl;

    @Value("${momo.refund-url}")
    private String refundUrl;

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
private final BookRepository bookRepository;
 private final PaymentRepository paymentRepository;
private final RestTemplate restTemplate = new RestTemplate();
   
public MomoService(OrderRepository orderRepository, CartRepository cartRepository, BookRepository bookRepository, PaymentRepository paymentRepository) {
    this.orderRepository = orderRepository;
    this.cartRepository = cartRepository;
    this.bookRepository = bookRepository;
    this.paymentRepository = paymentRepository;
}

 public MomoResponse createPayment(OrderDTO orderDTO) throws Exception {
        String requestId = UUID.randomUUID().toString();
        String orderId = orderDTO.getOrderCode();
        String amount = String.valueOf(orderDTO.getTotal().intValue());
        String orderInfo = "Payment order " + orderId;
        String extraData = "";

        String rawSignature =
            "accessKey=" + accessKey +
            "&amount=" + amount +
            "&extraData=" + extraData +
            "&ipnUrl=" + ipnUrl +
            "&orderId=" + orderId +
            "&orderInfo=" + orderInfo +
            "&partnerCode=" + partnerCode +
            "&redirectUrl=" + redirectUrl +
            "&requestId=" + requestId +
            "&requestType=captureWallet";

        // Tạo chữ ký HMAC SHA256
        String signature = HmacSHA256(rawSignature, secretKey);

        MomoRequest request = MomoRequest.builder()
                .partnerCode(partnerCode)
                .accessKey(accessKey)
                .requestId(requestId)
                .amount(amount)
                .orderId(orderId)
                .orderInfo(orderInfo)
                .redirectUrl(redirectUrl)
                .ipnUrl(ipnUrl)
                .extraData(extraData)
                .requestType("captureWallet")
                .lang("en")
                .signature(signature)
                .build();

        MomoResponse response = restTemplate.postForObject(
                momoUrl, request, MomoResponse.class
        );

        return response;
    }

    @Transactional
    public boolean handleSuccessfulPayment(Map<String, Object> payload) throws Exception {
        String orderId = (String) payload.get("orderId"); // orderCode

        Order order = orderRepository.findByOrderCode(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Đơn hàng không tìm thấy"));

        if (order.getStatus() != -1) return true;

        boolean enoughStock = true;

        // Kiểm tra tồn kho từng sản phẩm
        for (OrderDetail detail : order.getItems()) {
            Book book = detail.getBook();
            if (detail.getQuantity() > book.getStock()) {
                enoughStock = false;
                break;
            }
        }

        if (enoughStock) {
            // Còn hàng → cập nhật status = 0 và trừ tồn kho
            for (OrderDetail detail : order.getItems()) {
                Book book = detail.getBook();
                book.setStock(book.getStock() - detail.getQuantity());
                bookRepository.save(book);
            }
            order.setStatus(0);
            orderRepository.save(order);
           
         paymentRepository.save(Payment.builder()
                    .order(order)
                    .paymethod("momo")
                    .amount(order.getTotal())
                    .transactionId(String.valueOf(payload.get("transId")))
                    .status(1)
                    .build());
           
         Cart cart = cartRepository.findByUserId(order.getUser().getId()).orElse(null);
            if (cart != null) {
                cartRepository.delete(cart);
            }

            return true;

        } else {
            // Hết hàng → hoàn tiền và xóa đơn hàng
            payload.put("amount", order.getTotal().intValue());
            refundPayment(payload);

            // Xóa order
            orderRepository.delete(order);

            return false;
        }
    }

    public void refundPayment(Map<String, Object> payload) throws Exception {
        String transId = String.valueOf(payload.get("transId"));
        String amount = String.valueOf(payload.get("amount"));
        String originalOrderId = String.valueOf(payload.get("orderId"));

        String refundOrderId = originalOrderId + "_REFUND_" + System.currentTimeMillis();
        String requestId = UUID.randomUUID().toString();
        String description = "Hoàn tiền do một hoặc nhiều sản phẩm trong đơn hàng của bạn không đủ số lượng sách đã mua";

        String rawSignature = "accessKey=" + accessKey +
                "&amount=" + amount +
                "&description=" + description +
                "&orderId=" + refundOrderId +
                "&partnerCode=" + partnerCode +
                "&requestId=" + requestId +
                "&transId=" + transId;

        String signature = HmacSHA256(rawSignature, secretKey);

        Map<String, Object> requestBody = Map.of(
                "partnerCode", partnerCode,
                "accessKey", accessKey,
                "requestId", requestId,
                "amount", amount,
                "orderId", refundOrderId, 
                "transId", transId,
                "lang", "en",
                "description", description,
                "signature", signature
        );

        Map<String, Object> response = restTemplate.postForObject(refundUrl, requestBody, Map.class);

        if (response != null && !"0".equals(String.valueOf(response.get("resultCode")))) {
            throw new IllegalStateException("Hoàn tiền Momo thất bại " + response);
        }
    }

    private String HmacSHA256(String data, String key) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        sha256_HMAC.init(secret_key);
        byte[] hash = sha256_HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Hex.encodeHexString(hash);
    }

    public boolean validateSignature(Map<String, Object> payload) throws Exception {
        String orderId = (String) payload.get("orderId");
        String resultCode = String.valueOf(payload.get("resultCode"));
        String transId = String.valueOf(payload.get("transId"));

        String rawSignature =
            "partnerCode=" + partnerCode +
            "&accessKey=" + accessKey +
            "&requestId=" + payload.get("requestId") +
            "&amount=" + payload.get("amount") +
            "&orderId=" + orderId +
            "&orderInfo=" + payload.get("orderInfo") +
            "&orderType=" + payload.get("orderType") + 
            "&transId=" + transId +
            "&resultCode=" + resultCode +
            "&message=" + payload.get("message") +
            "&responseTime=" + payload.get("responseTime") +
            "&extraData=" + payload.get("extraData");


        String checkSignature = HmacSHA256(rawSignature, secretKey);
        String signature = (String) payload.get("signature");

        return checkSignature.equals(signature);
    }

}
