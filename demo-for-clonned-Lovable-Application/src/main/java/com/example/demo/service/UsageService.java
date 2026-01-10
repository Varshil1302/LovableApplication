package com.example.demo.service;

import com.example.demo.dto.subscription.PlanLimitsResponse;
import com.example.demo.dto.subscription.UsageTodayResponse;

public interface UsageService {

    UsageTodayResponse getTodayUsageOfUser(Long userId);

    PlanLimitsResponse getCurrentSubscriptionLimitsOfUser(Long userId);
}
