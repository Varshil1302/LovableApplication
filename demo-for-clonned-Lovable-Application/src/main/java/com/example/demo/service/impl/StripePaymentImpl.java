package com.example.demo.service.impl;

import com.example.demo.dto.subscription.CheckoutRequest;
import com.example.demo.dto.subscription.CheckoutResponse;
import com.example.demo.dto.subscription.PortalResponse;
import com.example.demo.entity.Plan;
import com.example.demo.entity.User;
import com.example.demo.enums.SubscriptionStatus;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.PlanRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtService;
import com.example.demo.service.PaymentProcessor;
import com.example.demo.service.SubscriptionService;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class StripePaymentImpl implements PaymentProcessor
{

    private final PlanRepository planRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final SubscriptionService subscriptionService;

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

        log.info("Param is created!....");

        try {
            String userStripeId=user.getStripeCustomerId();
            log.info("userStripeId:::{}",userStripeId);
            if(userStripeId==null || userStripeId.isEmpty())
            {
              params.setCustomerEmail(user.getEmail());
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

        Long userId = jwtService.getCurrentUser();
        User user=getUser(userId);
        String stripeCustomerId = user.getStripeCustomerId();

        if(stripeCustomerId==null || stripeCustomerId.isEmpty()){
            throw new BadRequestException("User does not have a Stripe Customer Id, UserId:"+userId);
        }

       com.stripe.param.billingportal.SessionCreateParams params =
               com.stripe.param.billingportal.SessionCreateParams.builder()
                        .setCustomer(stripeCustomerId)
                        .setReturnUrl("http://localhost:8080")
                        .build();

        try {
            var customerPortal= com.stripe.model.billingportal.Session.create(params);
            return new PortalResponse(customerPortal.getUrl());
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void handleWebhookEvent(String type, StripeObject stripeObject, Map<String, String> metadata) {
        log.info("Handling Event Type::{}",type);
        switch (type)
        {
            case "checkout.session.completed" -> handleCheckoutSession((Session) stripeObject,metadata);
            case "customer.subscription.deleted" -> handleSubscriptionDelete((Subscription) stripeObject);
            case "customer.subscription.updated" -> handleSubscriptionUpdate((Subscription) stripeObject);
            case "invoice.paid"  -> handleInvoicePaid((Invoice) stripeObject);
            case "invoice.payment_failed" -> handlePaymentfailed((Invoice) stripeObject);
            default -> log.debug("Invalid Event Detected:{}",type);
        }
    }

    private void handleCheckoutSession(Session session,Map<String, String> metadata) {

        if(session == null) {
            log.error("session object was null");
            return;
        }

       Long user_id=Long.parseLong(metadata.get("user_id"));

        log.info("User Id in checkout session is::{}",user_id);

       Long plan_id=Long.parseLong(metadata.get("plan_id"));

       log.info("Plan Id in checkout session is::{}",plan_id);

       String subscriptionId = session.getSubscription();
       String CustomerId = session.getCustomer();
       log.info("Stripe Customer Is is : {}",CustomerId);
       User user=getUser(user_id);
       if(user.getStripeCustomerId()==null)
       {
           user.setStripeCustomerId(CustomerId);
           userRepository.save(user);
       }
       subscriptionService.activateSubcription(user_id,plan_id,subscriptionId,CustomerId);
    }

    private User getUser(Long userId)
    {
        return userRepository.findById(userId).orElseThrow(()->new ResourceNotFoundException("User is not found: {} "+userId));
    }

    private void handleSubscriptionDelete(Subscription subscription){

        if(subscription==null){
            log.error("Subscription is null");
            return;
        }

        subscriptionService.cancelSubscription(subscription.getId());

    }
    private void handleSubscriptionUpdate(Subscription subscription)
    {

        if(subscription==null){
            log.error("Subscription is null");
            return;
        }

        SubscriptionStatus subscriptionStatus = mapStripeStatus(subscription.getStatus());
        if(subscriptionStatus==null)
        {
            log.warn("Invalid Status {} for subscription {}",subscription.getStatus(),subscriptionStatus.name());
            return;
        }

        SubscriptionItem subscriptionItem = subscription.getItems().getData().get(0);
        Instant periodStart = toInstant(subscriptionItem.getCurrentPeriodStart());
        Instant periodEnd = toInstant(subscriptionItem.getCurrentPeriodEnd());

        Long planId = resolvePlanId(subscriptionItem.getPrice());

        subscriptionService.updateSubscription(subscription.getId(),subscriptionStatus,periodStart,periodEnd,subscription.getCancelAtPeriodEnd(),planId);

    }

    private void handleInvoicePaid(Invoice invoice){

        String subScriptionId = extractSubscription(invoice);
        if(subScriptionId==null){
            log.warn("Subscription is invalid");
            return;
        }
        try {
            Subscription subscription = Subscription.retrieve(subScriptionId);
            SubscriptionItem item = subscription.getItems().getData().get(0);

            Instant periodStart= toInstant(item.getCurrentPeriodStart());
            Instant periodEnd = toInstant(item.getCurrentPeriodEnd());

            subscriptionService.renewSubscriptionPeriod(
                    subScriptionId,
                    periodStart,
                    periodEnd
            );
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }

    }
    private void handlePaymentfailed(Invoice invoice){

        String subScriptionId = extractSubscription(invoice);
        if(subScriptionId==null){
            log.warn("Subscription is invalid");
            return;
        }
        subscriptionService.marksubscriptionPastDue(subScriptionId);

    }

    private Long resolvePlanId(Price price)
    {
        if(price==null || price.getId()==null) return null;
        return planRepository.findByStripePriceId(price.getId())
                .map(Plan::getId)
                .orElse(null);
    }

    private Instant toInstant(Long currentPeriod)
    {
        return Instant.ofEpochSecond(currentPeriod);
    }

    private SubscriptionStatus mapStripeStatus(String status)
    {
        return switch (status){
            case "active" -> SubscriptionStatus.ACTIVE;
            case "trailing" -> SubscriptionStatus.TRIALING;
            case "past_due","unpaid","paused","incomplete_expired"-> SubscriptionStatus.PAST_DUE;
            case "canceled"-> SubscriptionStatus.CANCELED;
            case "incomplete"->SubscriptionStatus.INCOMPLETE;
            default -> {
                log.error("Invalid Status:{}",status);
                yield null;
            }
        };
    }
    private String extractSubscription(Invoice invoice)
    {
        if(invoice==null) return null;
        var parent = invoice.getParent();
        if(parent==null) return null;

        var subscriptionDetail=parent.getSubscriptionDetails();
        if(subscriptionDetail==null) return null;

        return subscriptionDetail.getSubscription();
    }

}
