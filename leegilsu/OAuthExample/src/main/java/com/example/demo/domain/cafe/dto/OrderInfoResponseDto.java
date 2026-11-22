package com.example.demo.domain.cafe.dto;

import com.example.demo.domain.cafe.entity.Order;
import com.example.demo.domain.cafe.entity.enums.CustomOption;
import com.example.demo.domain.cafe.entity.enums.OrderStatus;
import com.example.demo.domain.cafe.entity.enums.OrderType;
import com.example.demo.domain.cafe.entity.enums.PaymentMethod;
import com.example.demo.domain.cafe.entity.enums.SizeOption;
import com.example.demo.domain.cafe.entity.enums.Temperature;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record OrderInfoResponseDto(
        Long id,
        String customerPhoneNumber,
        Integer waitingNumber,
        OrderType orderType,
        String menuName,
        Temperature temperature,
        SizeOption sizeOption,
        CustomOption customOption,
        int quantity,
        int totalPrice,
        PaymentMethod paymentMethod,
        OrderStatus status,
        LocalDateTime createdAt
) {
    public static OrderInfoResponseDto from(Order order) {
        return OrderInfoResponseDto.builder()
                .id(order.getId())
                .customerPhoneNumber(order.getCustomer() != null ? order.getCustomer().getPhoneNumber() : null)
                .waitingNumber(order.getWaitingNumber())
                .orderType(order.getOrderType())
                .menuName(order.getMenuName())
                .temperature(order.getTemperature())
                .sizeOption(order.getSizeOption())
                .customOption(order.getCustomOption())
                .quantity(order.getQuantity())
                .totalPrice(order.getTotalPrice())
                .paymentMethod(order.getPaymentMethod())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .build();
    }
}
