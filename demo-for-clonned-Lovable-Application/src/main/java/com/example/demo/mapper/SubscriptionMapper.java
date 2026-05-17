package com.example.demo.mapper;

import com.example.demo.dto.subscription.PlanResponse;
import com.example.demo.dto.subscription.SubscriptionResponse;
import com.example.demo.entity.Plan;
import com.example.demo.entity.Subscription;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SubscriptionMapper {

    PlanResponse fromPlantoPlanResponse(Plan plan);

    SubscriptionResponse toSubscriptionResponse(Subscription subscription);

}
