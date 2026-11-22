package com.example.demo.domain.cafe.service;

import com.example.demo.domain.cafe.dto.OrderInfoResponseDto;
import com.example.demo.domain.cafe.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    @Transactional(readOnly = true)
    public List<OrderInfoResponseDto> getAllOrder() {
        return orderRepository.findAllWithCustomer()
                .stream()
                .map(OrderInfoResponseDto::from)
                .toList();
    }
}
