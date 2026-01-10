package com.example.demo.service.impl;

import com.example.demo.dto.subscription.PlanResponse;
import com.example.demo.service.PlanService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlanServiceImpl implements PlanService
{

    @Override
    public List<PlanResponse> getAllActivePlans() {
        return List.of();
    }
}
