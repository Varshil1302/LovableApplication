package com.example.demo.service.impl;

import com.example.demo.dto.subscription.CheckoutRequest;
import com.example.demo.dto.subscription.CheckoutResponse;
import com.example.demo.dto.subscription.PortalResponse;
import com.example.demo.dto.subscription.SubscriptionResponse;
import com.example.demo.service.SubscriptionService;
import org.springframework.stereotype.Service;

@Service
public class SubscriptionServiceImpl implements SubscriptionService
{

    @Override
    public SubscriptionResponse getCurrentSubscription(Long userId) {
        return null;
    }
}
