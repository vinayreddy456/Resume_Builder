package com.resume.airesumoptimizer.controller;

import com.resume.airesumoptimizer.service.RazorpayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins = "*")
public class PaymentController {

    @Autowired
    private RazorpayService razorpayService;

    @GetMapping("/plans")
    public ResponseEntity<Map<String, Object>> getPaymentPlans() {
        try {
            Map<String, Object> plans = razorpayService.getPaymentPlans();
            return ResponseEntity.ok(Map.of(
                "success", true,
                "plans", plans,
                "isConfigured", razorpayService.isConfigured()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }

    @PostMapping("/create-order")
    public ResponseEntity<Map<String, Object>> createOrder(@RequestBody Map<String, Object> request) {
        try {
            if (!razorpayService.isConfigured()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", "Payment service is not configured"
                ));
            }

            int amount = (Integer) request.get("amount");
            String currency = (String) request.getOrDefault("currency", "INR");
            String receipt = (String) request.getOrDefault("receipt", "receipt_" + System.currentTimeMillis());

            Map<String, Object> order = razorpayService.createOrder(amount, currency, receipt);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "order", order
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<Map<String, Object>> verifyPayment(@RequestBody Map<String, String> request) {
        try {
            String orderId = request.get("orderId");
            String paymentId = request.get("paymentId");
            String signature = request.get("signature");

            if (orderId == null || paymentId == null || signature == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", "Missing required parameters: orderId, paymentId, signature"
                ));
            }

            Map<String, Object> verification = razorpayService.verifyPayment(orderId, paymentId, signature);

            return ResponseEntity.ok(verification);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/config")
    public ResponseEntity<Map<String, Object>> getPaymentConfig() {
        try {
            Map<String, Object> config = Map.of(
                "isConfigured", razorpayService.isConfigured(),
                "keyId", razorpayService.getKeyId()
            );

            return ResponseEntity.ok(Map.of(
                "success", true,
                "config", config
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }
}
