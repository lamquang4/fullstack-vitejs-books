package com.bookstore.backend.service;
import org.springframework.beans.factory.annotation.Value;
import org.apache.commons.codec.binary.Hex;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;
import com.bookstore.backend.dto.MomoPaymentRequest;
import com.bookstore.backend.dto.MomoPaymentResponse;
import com.bookstore.backend.dto.OrderDTO;
import com.bookstore.backend.entities.Book;
import com.bookstore.backend.entities.Order;
import com.bookstore.backend.entities.OrderDetail;
import com.bookstore.backend.repository.BookRepository;
import com.bookstore.backend.repository.OrderRepository;
import jakarta.transaction.Transactional;
@Service
public class MomoPaymentService {

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
private final BookRepository bookRepository;
    public MomoPaymentService(OrderRepository orderRepository, BookRepository bookRepository) {
        this.orderRepository = orderRepository;
        this.bookRepository = bookRepository;
    }

    private final RestTemplate restTemplate = new RestTemplate();

 public MomoPaymentResponse createPayment(OrderDTO orderDTO) throws Exception {
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

        MomoPaymentRequest request = MomoPaymentRequest.builder()
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

        MomoPaymentResponse response = restTemplate.postForObject(
                momoUrl, request, MomoPaymentResponse.class
        );

        return response;
    }

    @Transactional
    public boolean handleSuccessfulPayment(Map<String, Object> payload) throws Exception {
        String orderId = (String) payload.get("orderId"); // orderCode

        Order order = orderRepository.findByOrderCode(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

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
            return true;

        } else {
            // Hết hàng → Hoàn tiền và xóa đơn hàng
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
    String description = "Refund due to insufficient stock for one or more items in your order";

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
            throw new RuntimeException("Refund failed: " + response);
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
