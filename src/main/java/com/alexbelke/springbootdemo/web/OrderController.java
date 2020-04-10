package com.alexbelke.springbootdemo.web;

import com.alexbelke.springbootdemo.model.Order;
import com.alexbelke.springbootdemo.model.Status;
import com.alexbelke.springbootdemo.repository.OrderRepository;
import com.alexbelke.springbootdemo.util.OrderModelAssembler;
import com.alexbelke.springbootdemo.util.exceptions.OrderNotFoundException;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
class OrderController {

	private final OrderRepository orderRepository;
	private final OrderModelAssembler assembler;

	OrderController(OrderRepository orderRepository,
					OrderModelAssembler assembler) {

		this.orderRepository = orderRepository;
		this.assembler = assembler;
	}

	@GetMapping("/orders")
	CollectionModel<EntityModel<Order>> all() {

		List<EntityModel<Order>> orders = orderRepository.findAll().stream()
				.map(assembler::toModel)
				.collect(Collectors.toList());

		return new CollectionModel<>(orders,
				linkTo(methodOn(OrderController.class).all()).withSelfRel());
	}

	@GetMapping("/orders/{id}")
	EntityModel<Order> one(@PathVariable Long id) {
		Order order = orderRepository.findById(id)
				.orElseThrow(() -> new OrderNotFoundException(id));

		return assembler.toModel(order);
	}

	@PostMapping("/orders")
	ResponseEntity<EntityModel<Order>> newOrder(@RequestBody Order order) {

		order.setStatus(Status.IN_PROGRESS);
		Order newOrder = orderRepository.save(order);

		return ResponseEntity
				.created(linkTo(methodOn(OrderController.class).one(newOrder.getId())).toUri())
				.body(assembler.toModel(newOrder));
	}
}
