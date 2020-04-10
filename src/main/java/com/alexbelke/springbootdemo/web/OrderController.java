package com.alexbelke.springbootdemo.web;

import com.alexbelke.springbootdemo.model.Order;
import com.alexbelke.springbootdemo.model.Status;
import com.alexbelke.springbootdemo.repository.OrderRepository;
import com.alexbelke.springbootdemo.util.OrderModelAssembler;
import com.alexbelke.springbootdemo.util.exceptions.OrderNotFoundException;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.mediatype.vnderrors.VndErrors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class OrderController {

	private final OrderRepository orderRepository;
	private final OrderModelAssembler assembler;

	public OrderController(OrderRepository orderRepository,
					OrderModelAssembler assembler) {

		this.orderRepository = orderRepository;
		this.assembler = assembler;
	}

	@GetMapping("/orders")
	public CollectionModel<EntityModel<Order>> all() {

		List<EntityModel<Order>> orders = orderRepository.findAll().stream()
				.map(assembler::toModel)
				.collect(Collectors.toList());

		return new CollectionModel<>(orders,
				linkTo(methodOn(OrderController.class).all()).withSelfRel());
	}

	@GetMapping("/orders/{id}")
	public EntityModel<Order> one(@PathVariable Long id) {
		Order order = orderRepository.findById(id)
				.orElseThrow(() -> new OrderNotFoundException(id));

		return assembler.toModel(order);
	}

	@PostMapping("/orders")
	public ResponseEntity<EntityModel<Order>> newOrder(@RequestBody Order order) {

		order.setStatus(Status.IN_PROGRESS);
		Order newOrder = orderRepository.save(order);

		return ResponseEntity
				.created(linkTo(methodOn(OrderController.class).one(newOrder.getId())).toUri())
				.body(assembler.toModel(newOrder));
	}

	@DeleteMapping("/orders/{id}/cancel")
	public ResponseEntity<RepresentationModel> cancel(@PathVariable Long id) {

		Order order = orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));

		if (order.getStatus() == Status.IN_PROGRESS) {
			order.setStatus(Status.CANCELLED);
			return ResponseEntity.ok(assembler.toModel(orderRepository.save(order)));
		}

		return ResponseEntity
				.status(HttpStatus.METHOD_NOT_ALLOWED)
				.body(new VndErrors.VndError("Method not allowed", "You can't cancel an order that is in the " + order.getStatus() + " status"));
	}

	@PutMapping("/orders/{id}/complete")
	public ResponseEntity<RepresentationModel> complete(@PathVariable Long id) {

		Order order = orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));

		if (order.getStatus() == Status.IN_PROGRESS) {
			order.setStatus(Status.COMPLETED);
			return ResponseEntity.ok(assembler.toModel(orderRepository.save(order)));
		}

		return ResponseEntity
				.status(HttpStatus.METHOD_NOT_ALLOWED)
				.body(new VndErrors.VndError("Method not allowed", "You can't complete an order that is in the " + order.getStatus() + " status"));
	}
}
