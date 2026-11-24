package com.example.demo.domain.cafe.repository;

import com.example.demo.domain.cafe.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.customer")
    List<Order> findAllWithCustomer();
}
