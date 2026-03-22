package com.example.demo.config;

import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.PostMapping;

@Configuration
public class PaymentConfig
{
    @Value("${stripe.secret}")
    private String stripeKey;

    @PostConstruct
    public void init()
    {
        Stripe.apiKey=stripeKey;
    }

}
