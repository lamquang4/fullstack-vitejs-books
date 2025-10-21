package com.bookstore.backend.controller;
import com.bookstore.backend.dto.OrderDTO;
import com.bookstore.backend.service.OrderService;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order")
@CrossOrigin(origins = "*")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // order cho admin
@GetMapping
public ResponseEntity<?> getOrders(
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "12") int limit,
        @RequestParam(required = false) String q,
        @RequestParam(required = false) Integer status
) {
    Page<OrderDTO> orderPage = orderService.getAllOrders(page, limit, q, status);

    return ResponseEntity.ok(Map.of(
        "orders", orderPage.getContent(),
        "totalPages", orderPage.getTotalPages(),
        "total", orderPage.getTotalElements()
    ));
}


@GetMapping("/{id}")
public ResponseEntity<OrderDTO> getOrderById(@PathVariable String id) {
    OrderDTO orderDTO = orderService.getOrderById(id);
    return ResponseEntity.ok(orderDTO);
}

@PutMapping("/{id}")
public ResponseEntity<OrderDTO> updateOrderStatus(
        @PathVariable("id") String orderId,
        @RequestParam Integer status
) {
    OrderDTO updatedOrder = orderService.updateOrderStatus(orderId, status);
    return ResponseEntity.ok(updatedOrder);
}


    // order cho customer
    @PostMapping("/user/{userId}")
    public ResponseEntity<OrderDTO> createOrder(
            @RequestBody OrderDTO orderDTO,
           @PathVariable String userId
    ) {
        OrderDTO createdOrder = orderService.createOrder(orderDTO, userId);
        return ResponseEntity.ok(createdOrder);
    }

    @GetMapping("/user/{userId}/{orderCode}")
public ResponseEntity<OrderDTO> getOrderByUserAndCode(
        @PathVariable String userId,
        @PathVariable String orderCode
) {
    OrderDTO order = orderService.getOrderByUserAndCode(userId, orderCode);
    return ResponseEntity.ok(order);
}

@GetMapping("/user/{userId}")
public ResponseEntity<?> getOrdersByUser(
        @PathVariable String userId,
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "12") int limit,
        @RequestParam(required = false) Integer status
) {
    Page<OrderDTO> orderPage = orderService.getOrdersByUserAndStatus(userId, page, limit, status);

    return ResponseEntity.ok(Map.of(
        "orders", orderPage.getContent(),
        "totalPages", orderPage.getTotalPages(),
        "total", orderPage.getTotalElements()
    ));
}
}
