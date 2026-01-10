package com.example.demo.service.impl;

import com.example.demo.dto.subscription.PlanLimitsResponse;
import com.example.demo.dto.subscription.UsageTodayResponse;
import com.example.demo.service.UsageService;
import org.springframework.stereotype.Service;

@Service
public class UsageServiceImpl implements UsageService
{

    @Override
    public UsageTodayResponse getTodayUsageOfUser(Long userId) {
        return null;
    }

    @Override
    public PlanLimitsResponse getCurrentSubscriptionLimitsOfUser(Long userId) {
        return null;
    }
}
