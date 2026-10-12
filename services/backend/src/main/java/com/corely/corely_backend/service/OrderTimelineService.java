package com.corely.corely_backend.service;

import com.corely.corely_backend.entity.Order;
import com.corely.corely_backend.entity.OrderTimeline;
import com.corely.corely_backend.enums.OrderStatus;
import com.corely.corely_backend.repository.OrderTimelineRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderTimelineService {

    OrderTimelineRepository orderTimelineRepository;

    public void recordStatusChange(Order order, OrderStatus from, OrderStatus to) {
        recordStatusChange(order, from, to, null);
    }

    public void recordStatusChange(Order order, OrderStatus from, OrderStatus to, String note) {
        String changedBy = "SYSTEM";
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && 
                !(auth instanceof org.springframework.security.authentication.AnonymousAuthenticationToken)) {
            changedBy = auth.getName();
        }

        OrderTimeline timeline = OrderTimeline.builder()
                .order(order)
                .fromStatus(from)
                .toStatus(to)
                .changedBy(changedBy)
                .note(note)
                .build();

        orderTimelineRepository.save(timeline);
    }
}