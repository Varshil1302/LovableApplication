package com.example.demo.service;

import com.example.demo.dto.subscription.CheckoutRequest;
import com.example.demo.dto.subscription.CheckoutResponse;
import com.example.demo.dto.subscription.PortalResponse;
import com.example.demo.dto.subscription.SubscriptionResponse;
import org.jspecify.annotations.Nullable;

public interface SubscriptionService
{

     SubscriptionResponse getCurrentSubscription(Long userId);

     CheckoutResponse createCheckoutSessionUrl(CheckoutRequest request, Long userId);

     PortalResponse openCustomerPortal(Long userId);
}
