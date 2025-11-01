package com.bookstore.backend.controller;
import com.bookstore.backend.dto.OrderDTO;
import com.bookstore.backend.service.OrderService;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
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
public ResponseEntity<?> getAllOrders(
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "12") int limit,
        @RequestParam(required = false) String q,
        @RequestParam(required = false) Integer status,
 @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end
) {
    Page<OrderDTO> orderPage = orderService.getAllOrders(page, limit, q, status, start, end);

    List<Integer> statuses = Arrays.asList(0, 3, 4, 5);
    Map<Integer, Long> totalByStatus = orderService.getOrderCountByStatus(statuses);

    return ResponseEntity.ok(Map.of(
        "orders", orderPage.getContent(),
        "totalPages", orderPage.getTotalPages(),
        "total", orderPage.getTotalElements(),
        "totalByStatus", totalByStatus
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

    @GetMapping("/statistics") 
    public ResponseEntity<Map<String, Object>> getOrderStats() {
        double totalRevenue = orderService.getTotalRevenue();
        double todayRevenue = orderService.getTodayRevenue();
        long totalSoldQuantity = orderService.getTotalSoldQuantity();
        long todaySoldQuantity = orderService.getTodaySoldQuantity();

        Map<String, Object> stats = Map.of(
                "totalRevenue", totalRevenue,
                "todayRevenue", todayRevenue,
                "totalSoldQuantity", totalSoldQuantity,
                "todaySoldQuantity", todaySoldQuantity
        );

        return ResponseEntity.ok(stats);
    }
}
