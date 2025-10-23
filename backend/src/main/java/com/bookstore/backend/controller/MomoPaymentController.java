package com.bookstore.backend.controller;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.bookstore.backend.dto.MomoPaymentResponse;
import com.bookstore.backend.dto.OrderDTO;
import com.bookstore.backend.service.MomoPaymentService;
import com.bookstore.backend.service.OrderService;
import lombok.RequiredArgsConstructor;
@RestController
@RequestMapping("/api/payment/momo")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MomoPaymentController {

    @Value("${frontend.url}")
    private String frontendUrl;

    private final MomoPaymentService momoPaymentService;
    private final OrderService orderService;

@PostMapping("/qr/{orderCode}")
public ResponseEntity<MomoPaymentResponse> payWithMomo(@PathVariable String orderCode) throws Exception {
    OrderDTO orderDTO = orderService.getOrderByOrderCode(orderCode);
    MomoPaymentResponse response = momoPaymentService.createPayment(orderDTO);
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

        boolean success = momoPaymentService.handleSuccessfulPayment(payload);

        if (success) {
            redirectUrl = frontendUrl + "/order-result?result=successfully&&orderCode=" + orderId;
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
