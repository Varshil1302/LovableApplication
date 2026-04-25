package com.example.demo.service;

import com.example.demo.dto.subscription.CheckoutRequest;
import com.example.demo.dto.subscription.CheckoutResponse;
import com.example.demo.dto.subscription.PortalResponse;
import com.example.demo.dto.subscription.SubscriptionResponse;
import com.example.demo.enums.SubscriptionStatus;
import org.jspecify.annotations.Nullable;

import java.time.Instant;

public interface SubscriptionService
{

     SubscriptionResponse getCurrentSubscription();

    void activateSubcription(Long userId, Long planId, String subscriptionId, String customerId);

    void updateSubscription(String subscriptionId, SubscriptionStatus subscriptionStatus, Instant periodStart, Instant periodEnd, Boolean cancelAtPeriodEnd, Long planId);

    void cancelSubscription(String subscriptionId);

    void renewSubscriptionPeriod(String subScriptionId, Instant periodStart, Instant periodEnd);

    void marksubscriptionPastDue(String subScriptionId);
}
