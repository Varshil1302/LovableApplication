package com.example.demo.service.impl;

import com.example.demo.dto.subscription.CheckoutRequest;
import com.example.demo.dto.subscription.CheckoutResponse;
import com.example.demo.dto.subscription.PlanResponse;
import com.example.demo.dto.subscription.PortalResponse;
import com.example.demo.entity.Plan;
import com.example.demo.entity.User;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.PlanRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtService;
import com.example.demo.service.PaymentProcessor;
import com.stripe.exception.StripeException;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class StripePaymentImpl implements PaymentProcessor
{

    private final PlanRepository planRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Override
    public CheckoutResponse createCheckoutSessionUrl(CheckoutRequest request) {

        Plan plan = planRepository.findById(request.planId()).orElseThrow();
        log.info("Plan is :::{}",plan.getName());

        Long userId = jwtService.getCurrentUser();
        log.info("User is::{}",userId);

        User user=userRepository.findById(userId).orElseThrow(()->new ResourceNotFoundException("User is not there.."));
        log.info("User::{}",user.getName());

        var params = SessionCreateParams.builder()
                .addLineItem(
                        SessionCreateParams.LineItem.builder().setPrice(plan.getStripePriceId()).setQuantity(1L).build())
                .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                .setSubscriptionData(
                        new SessionCreateParams.SubscriptionData.Builder()
                                .setBillingMode(SessionCreateParams.SubscriptionData.BillingMode.builder()
                                        .setType(SessionCreateParams.SubscriptionData.BillingMode.Type.FLEXIBLE)
                                        .build())
                                .build()
                )
                .setSuccessUrl("http://localhost:8080/success.html?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl("http://localhost:8080/cancel.html")
                .putMetadata("user_id",userId.toString())
                .putMetadata("plan_id",plan.getId().toString());

        try {
            String userStripeId=user.getStripeUserId();
            if(userStripeId==null || userStripeId.isEmpty())
            {

            }else{
                params.setCustomer(userStripeId);
            }
            Session session = Session.create(params.build());
            log.info("Session::{}",session.getPaymentStatus());
            log.info("Session Url::{}",session.getUrl());
            return new CheckoutResponse(session.getUrl());
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public PortalResponse openCustomerPortal() {
        return null;
    }

    @Override
    public void handleWebhookEvent(String type, StripeObject stripeObject, Map<String, String> metadata) {
        log.info("TODO");
    }
}
