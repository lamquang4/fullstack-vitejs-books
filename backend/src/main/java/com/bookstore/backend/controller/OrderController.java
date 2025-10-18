package com.bookstore.backend.controller;
import com.bookstore.backend.entities.Order;
import com.bookstore.backend.service.OrderService;
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

@GetMapping("/{id}")
public ResponseEntity<Order> getOrderById(@PathVariable String id) {
    return orderService.getOrderById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
}


}
