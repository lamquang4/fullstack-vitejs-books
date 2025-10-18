package com.bookstore.backend.service;
import com.bookstore.backend.entities.Order;
import com.bookstore.backend.repository.OrderRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;

    }



    public Optional<Order> getOrderById(String id) {
        return orderRepository.findById(id);
    }



}
