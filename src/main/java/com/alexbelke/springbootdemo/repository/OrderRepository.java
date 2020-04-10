package com.alexbelke.springbootdemo.repository;

import com.alexbelke.springbootdemo.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
