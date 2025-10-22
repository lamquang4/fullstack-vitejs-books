package com.bookstore.backend.service;
import com.bookstore.backend.dto.OrderDTO;
import com.bookstore.backend.dto.OrderDetailDTO;
import com.bookstore.backend.entities.Book;
import com.bookstore.backend.entities.Cart;
import com.bookstore.backend.entities.Order;
import com.bookstore.backend.entities.OrderDetail;
import com.bookstore.backend.entities.User;
import com.bookstore.backend.repository.BookRepository;
import com.bookstore.backend.repository.CartRepository;
import com.bookstore.backend.repository.OrderRepository;
import com.bookstore.backend.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.stream.Collectors;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Random;

import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
 private final CartRepository cartRepository;
    public OrderService(OrderRepository orderRepository,
                        UserRepository userRepository,
                        BookRepository bookRepository, CartRepository cartRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.cartRepository = cartRepository;
    }

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
public Page<OrderDTO> getAllOrders(int page, int limit, String orderCode, Integer status) {
    Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("createdAt").descending());
    Page<Order> orderPage;

    if ((orderCode != null && !orderCode.isEmpty()) && status != null) {
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

public OrderDTO getOrderById(String id) {
    Order order = orderRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
    return convertToDTO(order);
}

public OrderDTO updateOrderStatus(String orderId, Integer status) {
    Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));

if (status == 4 && order.getStatus() != 4) {
    if (order.getItems() != null) {
        for (OrderDetail detail : order.getItems()) {
            Book book = detail.getBook();
            book.setStock(book.getStock() + detail.getQuantity());
            bookRepository.save(book);
        }
    }
}

    order.setStatus(status);
    Order updatedOrder = orderRepository.save(order);
    return convertToDTO(updatedOrder);
}

// order cho customer
public OrderDTO createOrder(OrderDTO orderDTO, String userId) {
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

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
                    .orElseThrow(() -> new RuntimeException("Book not found"));

            // Kiểm tra tồn kho
            if ("cod".equalsIgnoreCase(orderDTO.getPaymethod()) && d.getQuantity() > book.getStock()) {
                throw new RuntimeException(book.getTitle() + " not enough stock");
            }

            // Trừ tồn kho nếu thanh toán COD
            if (status == 0) {
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
Cart cart = cartRepository.findByUserId(userId)
        .orElse(null);
if (cart != null) {
    cartRepository.delete(cart);
}

    return convertToDTO(savedOrder);
}

    public OrderDTO getOrderByUserAndCode(String userId, String orderCode) {
    Order order = orderRepository.findByUserIdAndOrderCode(userId, orderCode)
            .orElseThrow(() -> new RuntimeException("Order not found"));
    return convertToDTO(order);
}

public Page<OrderDTO> getOrdersByUserAndStatus(String userId, int page, int limit, Integer status) {
    Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("createdAt").descending());
    Page<Order> orderPage;

    if (status != null && status >= 0) {
        orderPage = orderRepository.findByUserIdAndStatus(userId, status, pageable);
    } else {
        orderPage = orderRepository.findByUserId(userId, pageable);
    }

    return orderPage.map(this::convertToDTO);
}
}
