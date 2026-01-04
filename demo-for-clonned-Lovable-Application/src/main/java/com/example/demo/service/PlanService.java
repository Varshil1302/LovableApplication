package com.example.demo.service;

import com.example.demo.dto.subscription.PlanResponse;
import org.jspecify.annotations.Nullable;

import java.util.List;

public interface PlanService
{

     List<PlanResponse> getAllActivePlans();
}
