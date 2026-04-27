package com.resume.airesumoptimizer.service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import io.github.cdimascio.dotenv.Dotenv;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class RazorpayService {

    @Value("${razorpay.key.id:}")
    private String razorpayKeyId;

    @Value("${razorpay.key.secret:}")
    private String razorpayKeySecret;

    private RazorpayClient razorpayClient;

    public RazorpayService() {
        // Load environment variables if not already loaded
        if (razorpayKeyId == null) {
            Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
            razorpayKeyId = dotenv.get("RAZORPAY_KEY_ID");
            razorpayKeySecret = dotenv.get("RAZORPAY_KEY_SECRET");
        }
        
        try {
            if (razorpayKeyId != null && razorpayKeySecret != null) {
                this.razorpayClient = new RazorpayClient(razorpayKeyId, razorpayKeySecret);
            }
        } catch (RazorpayException e) {
            throw new RuntimeException("Failed to initialize Razorpay client: " + e.getMessage(), e);
        }
    }

    public Map<String, Object> createOrder(int amount, String currency, String receipt) {
        try {
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amount * 100); // Amount in paise
            orderRequest.put("currency", currency);
            orderRequest.put("receipt", receipt);
            orderRequest.put("payment_capture", 1);

            Order order = razorpayClient.orders.create(orderRequest);

            Map<String, Object> response = new HashMap<>();
            response.put("orderId", order.get("id"));
            response.put("amount", order.get("amount"));
            response.put("currency", order.get("currency"));
            response.put("receipt", order.get("receipt"));
            response.put("keyId", razorpayKeyId);

            return response;

        } catch (RazorpayException e) {
            throw new RuntimeException("Failed to create Razorpay order: " + e.getMessage(), e);
        }
    }

    public Map<String, Object> verifyPayment(String orderId, String paymentId, String signature) {
        try {
            // Verify payment signature
            String payload = orderId + "|" + paymentId;
            String expectedSignature = org.apache.commons.codec.digest.HmacUtils.hmacSha256Hex(
                razorpayKeySecret, payload);

            boolean isSignatureValid = expectedSignature.equals(signature);

            Map<String, Object> response = new HashMap<>();
            response.put("success", isSignatureValid);
            response.put("message", isSignatureValid ? "Payment verified successfully" : "Payment verification failed");

            if (isSignatureValid) {
                response.put("orderId", orderId);
                response.put("paymentId", paymentId);
            }

            return response;

        } catch (Exception e) {
            throw new RuntimeException("Failed to verify Razorpay payment: " + e.getMessage(), e);
        }
    }

    public Map<String, Object> getPaymentPlans() {
        Map<String, Object> plans = new HashMap<>();
        
        // Basic plan - Free
        Map<String, Object> basicPlan = new HashMap<>();
        basicPlan.put("id", "basic");
        basicPlan.put("name", "Basic");
        basicPlan.put("price", 0);
        basicPlan.put("currency", "INR");
        basicPlan.put("features", new String[]{
            "Upload and parse resume",
            "Basic job description analysis",
            "Simple skill gap analysis",
            "Limited to 3 optimizations per month"
        });
        plans.put("basic", basicPlan);

        // Premium plan - Paid
        Map<String, Object> premiumPlan = new HashMap<>();
        premiumPlan.put("id", "premium");
        premiumPlan.put("name", "Premium");
        premiumPlan.put("price", 99); // ₹99
        premiumPlan.put("currency", "INR");
        premiumPlan.put("duration", "monthly");
        premiumPlan.put("features", new String[]{
            "Unlimited resume uploads",
            "Advanced job description analysis",
            "Comprehensive skill gap analysis",
            "AI-powered resume rewriting",
            "ATS optimization",
            "Professional formatting",
            "Cover letter generation",
            "Priority support",
            "Unlimited optimizations"
        });
        plans.put("premium", premiumPlan);

        // Enterprise plan - Paid
        Map<String, Object> enterprisePlan = new HashMap<>();
        enterprisePlan.put("id", "enterprise");
        enterprisePlan.put("name", "Enterprise");
        enterprisePlan.put("price", 299); // ₹299
        enterprisePlan.put("currency", "INR");
        enterprisePlan.put("duration", "monthly");
        enterprisePlan.put("features", new String[]{
            "All Premium features",
            "Multiple user accounts",
            "Team collaboration tools",
            "Custom branding",
            "API access",
            "Advanced analytics",
            "Dedicated account manager",
            "Custom integrations"
        });
        plans.put("enterprise", enterprisePlan);

        return plans;
    }

    public boolean isConfigured() {
        return razorpayKeyId != null && razorpayKeySecret != null && razorpayClient != null;
    }

    public String getKeyId() {
        return razorpayKeyId;
    }
}
