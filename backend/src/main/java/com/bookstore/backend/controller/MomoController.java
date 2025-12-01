package com.bookstore.backend.controller;

import com.bookstore.backend.dto.MomoResponse;
import com.bookstore.backend.dto.OrderDTO;
import com.bookstore.backend.service.MomoService;
import com.bookstore.backend.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
@RestController
@RequestMapping("/api/payment/momo")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MomoController {

    @Value("${frontend.url}")
    private String frontendUrl;

    private final MomoService momoService;
    private final OrderService orderService;

    @PostMapping("/qr/{orderCode}")
    public ResponseEntity<MomoResponse> payWithMomo(@PathVariable String orderCode) throws Exception {
        OrderDTO orderDTO = orderService.getOrderByOrderCode(orderCode);
        MomoResponse response = momoService.createPayment(orderDTO);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/redirect")
    public ResponseEntity<Void> handleRedirect(
            @RequestParam(required = false) String resultCode,
            @RequestParam(required = false) String orderId,
            @RequestParam(required = false) String transId,
            @RequestParam(required = false) String message
    ) throws Exception {
        String redirectUrl;

        if ("0".equals(resultCode)) {
            Map<String, Object> payload = new HashMap<>();
            payload.put("orderId", orderId);
            payload.put("resultCode", resultCode);
            payload.put("transId", transId != null ? transId : "");
            payload.put("message", message != null ? message : "");

            boolean success = momoService.handleSuccessfulPayment(payload);

            if (success) {
                redirectUrl = frontendUrl + "/order-result?result=successful&&orderCode=" + orderId;
            } else {
                redirectUrl = frontendUrl + "/order-result?result=fail";
            }
        } else {
            redirectUrl = frontendUrl + "/";
        }

        return ResponseEntity.status(302)
                .header("Location", redirectUrl)
                .build();
    }
}
