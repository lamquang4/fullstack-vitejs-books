package com.bookstore.backend.service;
import com.bookstore.backend.dto.OrderDTO;
import com.bookstore.backend.dto.OrderDetailDTO;
import com.bookstore.backend.entities.Book;
import com.bookstore.backend.entities.Cart;
import com.bookstore.backend.entities.Order;
import com.bookstore.backend.entities.Payment;
import com.bookstore.backend.entities.OrderDetail;
import com.bookstore.backend.entities.User;
import com.bookstore.backend.repository.BookRepository;
import com.bookstore.backend.repository.CartRepository;
import com.bookstore.backend.repository.OrderRepository;
import com.bookstore.backend.repository.UserRepository;
import com.bookstore.backend.utils.ValidationUtils;
import com.bookstore.backend.repository.PaymentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.stream.Collectors;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import java.util.Random;
import org.springframework.data.domain.Sort;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

@Service
public class OrderService {

  private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final CartRepository cartRepository;
    private final PaymentRepository paymentRepository;
    private final MomoService momoService; 

    public OrderService(OrderRepository orderRepository,
                        UserRepository userRepository,
                        BookRepository bookRepository, 
                        CartRepository cartRepository,
                       PaymentRepository paymentRepository,
                        MomoService momoService) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.cartRepository = cartRepository;
        this.paymentRepository = paymentRepository;
        this.momoService = momoService; 
    }

    // tạo order code
    private String generateOrderCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < 6; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    private OrderDetailDTO convertToDetailDTO(OrderDetail detail) {
        OrderDetailDTO dto = new OrderDetailDTO();
        dto.setBookId(detail.getBook().getId());
        dto.setTitle(detail.getBook().getTitle());

        if (detail.getBook().getImages() != null && !detail.getBook().getImages().isEmpty()) {
            List<String> images = detail.getBook().getImages().stream()
                    .sorted(Comparator.comparing(img -> img.getCreatedAt()))
                    .map(img -> img.getImage())
                    .collect(Collectors.toList());
            dto.setImages(images);
        } else {
            dto.setImages(Collections.emptyList());
        }

        dto.setQuantity(detail.getQuantity());
        dto.setPrice(detail.getPrice());
        dto.setDiscount(detail.getDiscount());

        return dto;
    }

    private OrderDTO convertToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setOrderCode(order.getOrderCode());
        dto.setFullname(order.getFullname());
        dto.setPhone(order.getPhone());
        dto.setSpeaddress(order.getSpeaddress());
        dto.setCity(order.getCity());
        dto.setWard(order.getWard());
        dto.setPaymethod(order.getPaymethod());
        dto.setStatus(order.getStatus());
        dto.setTotal(order.getTotal());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setAccountEmail(order.getUser() != null ? order.getUser().getEmail() : null);

        if (order.getItems() != null && !order.getItems().isEmpty()) {
            List<OrderDetailDTO> detailDTOs = order.getItems().stream()
                    .map(this::convertToDetailDTO)
                    .collect(Collectors.toList());
            dto.setItems(detailDTOs);
        } else {
            dto.setItems(Collections.emptyList());
        }

        return dto;
    }

    // order cho admin

    // lấy tất cả orders
    public Page<OrderDTO> getAllOrders(int page, int limit, String orderCode, Integer status, LocalDate start, LocalDate end) {
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("createdAt").descending());
        Page<Order> orderPage;

  LocalDateTime startDateTime = null;
    LocalDateTime endDateTime = null;
    if (start != null) startDateTime = start.atStartOfDay();
    if (end != null) endDateTime = end.atTime(LocalTime.MAX);

    if ((orderCode != null && !orderCode.isEmpty()) && status != null && startDateTime != null && endDateTime != null) {
        orderPage = orderRepository.findByOrderCodeContainingIgnoreCaseAndStatusAndCreatedAtBetween(orderCode, status, startDateTime, endDateTime, pageable);
    } else if ((orderCode != null && !orderCode.isEmpty()) && startDateTime != null && endDateTime != null) {
        orderPage = orderRepository.findByOrderCodeContainingIgnoreCaseAndCreatedAtBetween(orderCode, startDateTime, endDateTime, pageable);
    } else if (status != null && startDateTime != null && endDateTime != null) {
        orderPage = orderRepository.findByStatusAndCreatedAtBetween(status, startDateTime, endDateTime, pageable);
    } else if (startDateTime != null && endDateTime != null) {
        orderPage = orderRepository.findByCreatedAtBetween(startDateTime, endDateTime, pageable);
    } else if ((orderCode != null && !orderCode.isEmpty()) && status != null) {
        orderPage = orderRepository.findByOrderCodeContainingIgnoreCaseAndStatus(orderCode, status, pageable);
    } else if (orderCode != null && !orderCode.isEmpty()) {
        orderPage = orderRepository.findByOrderCodeContainingIgnoreCase(orderCode, pageable);
    } else if (status != null) {
        orderPage = orderRepository.findByStatus(status, pageable);
    } else {
        orderPage = orderRepository.findAll(pageable);
    }


        return orderPage.map(this::convertToDTO);
    }

    // đếm số lượng order có status đó
    public Map<Integer, Long> getOrderCountByStatus(List<Integer> statuses) {
        List<Object[]> results = orderRepository.countOrdersByStatus(statuses);
        Map<Integer, Long> map = new HashMap<>();
        for (Object[] row : results) {
            Integer status = (Integer) row[0];
            Long total = (Long) row[1];
            map.put(status, total);
        }
        
        for (Integer s : statuses) {
            map.putIfAbsent(s, 0L);
        }
        return map;
    }

    // lấy 1 order theo id
    public OrderDTO getOrderById(String id) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Đơn hàng không tìm thấy"));
        return convertToDTO(order);
    }

    // cập nhật status của order
    @Transactional
    public OrderDTO updateOrderStatus(String orderId, Integer status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Đơn hàng không tìm thấy"));

        if ((status == 4 || status == 5) && order.getStatus() != 4 && order.getStatus() != 5) {
            // trả tồn kho
            if (order.getItems() != null) {
                for (OrderDetail detail : order.getItems()) {
                    Book book = detail.getBook();
                    book.setStock(book.getStock() + detail.getQuantity());
                    bookRepository.save(book);
                }
            }
    
            // hoàn tiền momo
    if ("momo".equalsIgnoreCase(order.getPaymethod())) {
        Payment payment = paymentRepository.findFirstByOrder_OrderCode(order.getOrderCode())
                .orElseThrow(() -> new EntityNotFoundException("Thanh toán không tìm thấy"));

        Map<String, Object> payload = new HashMap<>();
        payload.put("orderId", order.getOrderCode());
        payload.put("amount", order.getTotal().intValue());
        payload.put("transId", payment.getTransactionId()); 

        try {
            momoService.refundPayment(payload);
        } catch (Exception e) {
            throw new RuntimeException("Hoàn tiền Momo thất bại " + e.getMessage(), e);
        }

        Payment refundPayment = Payment.builder()
                .order(order)
                .paymethod("momo")
                .amount(order.getTotal())
                .transactionId(payment.getTransactionId())
                .status(0)
                .build();

        paymentRepository.save(refundPayment);
    }
    }

        order.setStatus(status);
        Order updatedOrder = orderRepository.save(order);
        return convertToDTO(updatedOrder);
    }

    // order cho customer

    // tạo đơn hàng mặc định status = 0
    public OrderDTO createOrder(OrderDTO orderDTO, String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Người dùng không tìm thấy"));

        if (!ValidationUtils.validatePhone(orderDTO.getPhone())) {
            throw new IllegalArgumentException("Số điện thoại không hợp lệ");
        }

        // cod -> status = 0 (chờ xác nhận)
        // momo -> status = -1 (chờ thanh toán)
        int status = "cod".equalsIgnoreCase(orderDTO.getPaymethod()) ? 0 : -1; 

        Order order = Order.builder()
                .orderCode(generateOrderCode())
                .fullname(orderDTO.getFullname())
                .phone(orderDTO.getPhone())
                .speaddress(orderDTO.getSpeaddress())
                .city(orderDTO.getCity())
                .ward(orderDTO.getWard())
                .paymethod(orderDTO.getPaymethod())
                .status(status)
                .user(user)
                .createdAt(LocalDateTime.now())
                .build();

        if (orderDTO.getItems() != null && !orderDTO.getItems().isEmpty()) {
            List<OrderDetail> details = orderDTO.getItems().stream().map(d -> {
                Book book = bookRepository.findById(d.getBookId())
                        .orElseThrow(() -> new EntityNotFoundException("Sách không tìm thấy"));

                // Kiểm tra số lượng
            if (d.getQuantity() > book.getStock()) {
                    throw new IllegalArgumentException(book.getTitle() + " không đủ số lượng");
                }

                // Nếu thanh toán COD trừ số lượng, thanh toán Momo không trừ tồn kho
            if ("cod".equalsIgnoreCase(orderDTO.getPaymethod())) {
                    book.setStock(book.getStock() - d.getQuantity());
                    bookRepository.save(book);
                }

            return OrderDetail.builder()
                        .book(book)
                        .order(order)
                        .quantity(d.getQuantity())
                        .price(d.getPrice())
                        .discount(d.getDiscount())
                        .build();
            }).collect(Collectors.toList());

            // Tính tổng tiền
            double total = details.stream()
                    .mapToDouble(d -> {
                        double finalPrice = d.getDiscount() > 0
                                ? (d.getPrice() - d.getDiscount())
                                : d.getPrice();
                        return finalPrice * d.getQuantity();
                    })
                    .sum();

            order.setItems(details);
            order.setTotal(total);
        } else {
            order.setTotal(0.0);
        }

        Order savedOrder = orderRepository.save(order);

        // xóa giỏ hàng

        Cart cart = cartRepository.findByUserId(userId).orElse(null);
        if (cart != null) {
            cartRepository.delete(cart);
        }

        return convertToDTO(savedOrder);
    }

    // láy order của customer theo user id và order code
    public OrderDTO getOrderByUserAndCode(String userId, String orderCode) {
        Order order = orderRepository.findByUserIdAndOrderCode(userId, orderCode)
                .orElseThrow(() -> new EntityNotFoundException("Đơn hàng không tìm thấy"));
        return convertToDTO(order);
    }

    // lấy các orders theo user id và lọc theo status
    public Page<OrderDTO> getOrdersByUserAndStatus(String userId, int page, int limit, Integer status) {
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("createdAt").descending());
        Page<Order> orderPage;

        // không lấy order có status = -1
        if (status != null && status >= 0) {
            orderPage = orderRepository.findByUserIdAndStatus(userId, status, pageable);
        } else {
            orderPage = orderRepository.findByUserIdAndStatusGreaterThanEqual(userId, 0, pageable);
        }

        return orderPage.map(this::convertToDTO);
    }

    public OrderDTO getOrderByOrderCode(String orderCode) {
        Order order = orderRepository.findByOrderCode(orderCode)
            .orElseThrow(() -> new EntityNotFoundException("Đơn hàng không tìm thấy"));
        return convertToDTO(order);
    }

    // Tổng doanh thu của tất cả orders có status = 3
    public double getTotalRevenue() {
        Double total = orderRepository.sumTotalByStatus(3);
        return total != null ? total : 0.0;
    }

     // Doanh thu của orders có status = 3 trong ngày
    public double getTodayRevenue() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(23, 59, 59);
        Double total = orderRepository.sumTotalByStatusAndCreatedAtBetween(3, startOfDay, endOfDay);
        return total != null ? total : 0.0;
    }

     // Tổng số lượng sản phẩm đã bán của orders có status = 3
    public long getTotalSoldQuantity() {
        Long totalQty = orderRepository.sumQuantityByStatus(3);
        return totalQty != null ? totalQty : 0L;
    }

     // Tổng số lượng sản phẩm đã bán của orders có status = 3 trong ngày
    public long getTodaySoldQuantity() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(23, 59, 59);
        Long totalQty = orderRepository.sumQuantityByStatusAndCreatedAtBetween(3, startOfDay, endOfDay);
        return totalQty != null ? totalQty : 0L;
    }

  // Chạy mỗi 30 phút để xóa order status -1 chưa thanh toán quá 90 phút
    @Scheduled(fixedRate = 30 * 60 * 1000)
    public void cleanupUnpaidOrders() {
        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(90);

        // Tìm các đơn có status = -1 (chưa thanh toán) và quá 90 phút
        List<Order> expiredOrders = orderRepository.findByStatusAndCreatedAtBefore(-1, cutoff);

        if (!expiredOrders.isEmpty()) {
            orderRepository.deleteAll(expiredOrders);
        }
    }
}
