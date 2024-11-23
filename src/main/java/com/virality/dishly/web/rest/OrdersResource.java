package com.virality.dishly.web.rest;

import com.virality.dishly.domain.Orders;
import com.virality.dishly.repository.OrdersRepository;
import com.virality.dishly.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.virality.dishly.domain.Orders}.
 */
@RestController
@RequestMapping("/api/orders")
@Transactional
public class OrdersResource {

    private static final Logger LOG = LoggerFactory.getLogger(OrdersResource.class);

    private static final String ENTITY_NAME = "orders";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final OrdersRepository ordersRepository;

    public OrdersResource(OrdersRepository ordersRepository) {
        this.ordersRepository = ordersRepository;
    }

    /**
     * {@code POST  /orders} : Create a new orders.
     *
     * @param orders the orders to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new orders, or with status {@code 400 (Bad Request)} if the orders has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<Orders>> createOrders(@Valid @RequestBody Orders orders) throws URISyntaxException {
        LOG.debug("REST request to save Orders : {}", orders);
        if (orders.getId() != null) {
            throw new BadRequestAlertException("A new orders cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return ordersRepository
            .save(orders)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/orders/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /orders/:id} : Updates an existing orders.
     *
     * @param id the id of the orders to save.
     * @param orders the orders to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated orders,
     * or with status {@code 400 (Bad Request)} if the orders is not valid,
     * or with status {@code 500 (Internal Server Error)} if the orders couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<Orders>> updateOrders(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Orders orders
    ) throws URISyntaxException {
        LOG.debug("REST request to update Orders : {}, {}", id, orders);
        if (orders.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, orders.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return ordersRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return ordersRepository
                    .save(orders)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /orders/:id} : Partial updates given fields of an existing orders, field will ignore if it is null
     *
     * @param id the id of the orders to save.
     * @param orders the orders to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated orders,
     * or with status {@code 400 (Bad Request)} if the orders is not valid,
     * or with status {@code 404 (Not Found)} if the orders is not found,
     * or with status {@code 500 (Internal Server Error)} if the orders couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Orders>> partialUpdateOrders(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Orders orders
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Orders partially : {}, {}", id, orders);
        if (orders.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, orders.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return ordersRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Orders> result = ordersRepository
                    .findById(orders.getId())
                    .map(existingOrders -> {
                        if (orders.getStatus() != null) {
                            existingOrders.setStatus(orders.getStatus());
                        }
                        if (orders.getUpdatedAt() != null) {
                            existingOrders.setUpdatedAt(orders.getUpdatedAt());
                        }
                        if (orders.getCreatedAt() != null) {
                            existingOrders.setCreatedAt(orders.getCreatedAt());
                        }
                        if (orders.getSum() != null) {
                            existingOrders.setSum(orders.getSum());
                        }
                        if (orders.getPaymentMethod() != null) {
                            existingOrders.setPaymentMethod(orders.getPaymentMethod());
                        }
                        if (orders.getPaymentStatus() != null) {
                            existingOrders.setPaymentStatus(orders.getPaymentStatus());
                        }
                        if (orders.getTransactionId() != null) {
                            existingOrders.setTransactionId(orders.getTransactionId());
                        }
                        if (orders.getOrderStatus() != null) {
                            existingOrders.setOrderStatus(orders.getOrderStatus());
                        }

                        return existingOrders;
                    })
                    .flatMap(ordersRepository::save);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, res.getId().toString()))
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /orders} : get all the orders.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of orders in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<Orders>> getAllOrders() {
        LOG.debug("REST request to get all Orders");
        return ordersRepository.findAll().collectList();
    }

    /**
     * {@code GET  /orders} : get all the orders as a stream.
     * @return the {@link Flux} of orders.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Orders> getAllOrdersAsStream() {
        LOG.debug("REST request to get all Orders as a stream");
        return ordersRepository.findAll();
    }

    /**
     * {@code GET  /orders/:id} : get the "id" orders.
     *
     * @param id the id of the orders to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the orders, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<Orders>> getOrders(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Orders : {}", id);
        Mono<Orders> orders = ordersRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(orders);
    }

    /**
     * {@code DELETE  /orders/:id} : delete the "id" orders.
     *
     * @param id the id of the orders to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteOrders(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Orders : {}", id);
        return ordersRepository
            .deleteById(id)
            .then(
                Mono.just(
                    ResponseEntity.noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
                        .build()
                )
            );
    }
}
