package com.virality.dishly.web.rest;

import com.virality.dishly.domain.OrderItem;
import com.virality.dishly.repository.OrderItemRepository;
import com.virality.dishly.web.rest.errors.BadRequestAlertException;
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
 * REST controller for managing {@link com.virality.dishly.domain.OrderItem}.
 */
@RestController
@RequestMapping("/api/order-items")
@Transactional
public class OrderItemResource {

    private static final Logger LOG = LoggerFactory.getLogger(OrderItemResource.class);

    private static final String ENTITY_NAME = "orderItem";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final OrderItemRepository orderItemRepository;

    public OrderItemResource(OrderItemRepository orderItemRepository) {
        this.orderItemRepository = orderItemRepository;
    }

    /**
     * {@code POST  /order-items} : Create a new orderItem.
     *
     * @param orderItem the orderItem to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new orderItem, or with status {@code 400 (Bad Request)} if the orderItem has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<OrderItem>> createOrderItem(@RequestBody OrderItem orderItem) throws URISyntaxException {
        LOG.debug("REST request to save OrderItem : {}", orderItem);
        if (orderItem.getId() != null) {
            throw new BadRequestAlertException("A new orderItem cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return orderItemRepository
            .save(orderItem)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/order-items/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /order-items/:id} : Updates an existing orderItem.
     *
     * @param id the id of the orderItem to save.
     * @param orderItem the orderItem to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated orderItem,
     * or with status {@code 400 (Bad Request)} if the orderItem is not valid,
     * or with status {@code 500 (Internal Server Error)} if the orderItem couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<OrderItem>> updateOrderItem(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody OrderItem orderItem
    ) throws URISyntaxException {
        LOG.debug("REST request to update OrderItem : {}, {}", id, orderItem);
        if (orderItem.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, orderItem.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return orderItemRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return orderItemRepository
                    .save(orderItem)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /order-items/:id} : Partial updates given fields of an existing orderItem, field will ignore if it is null
     *
     * @param id the id of the orderItem to save.
     * @param orderItem the orderItem to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated orderItem,
     * or with status {@code 400 (Bad Request)} if the orderItem is not valid,
     * or with status {@code 404 (Not Found)} if the orderItem is not found,
     * or with status {@code 500 (Internal Server Error)} if the orderItem couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<OrderItem>> partialUpdateOrderItem(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody OrderItem orderItem
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update OrderItem partially : {}, {}", id, orderItem);
        if (orderItem.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, orderItem.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return orderItemRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<OrderItem> result = orderItemRepository
                    .findById(orderItem.getId())
                    .map(existingOrderItem -> {
                        if (orderItem.getQuantity() != null) {
                            existingOrderItem.setQuantity(orderItem.getQuantity());
                        }
                        if (orderItem.getPrice() != null) {
                            existingOrderItem.setPrice(orderItem.getPrice());
                        }
                        if (orderItem.getTotalPrice() != null) {
                            existingOrderItem.setTotalPrice(orderItem.getTotalPrice());
                        }

                        return existingOrderItem;
                    })
                    .flatMap(orderItemRepository::save);

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
     * {@code GET  /order-items} : get all the orderItems.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of orderItems in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<OrderItem>> getAllOrderItems() {
        LOG.debug("REST request to get all OrderItems");
        return orderItemRepository.findAll().collectList();
    }

    /**
     * {@code GET  /order-items} : get all the orderItems as a stream.
     * @return the {@link Flux} of orderItems.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<OrderItem> getAllOrderItemsAsStream() {
        LOG.debug("REST request to get all OrderItems as a stream");
        return orderItemRepository.findAll();
    }

    /**
     * {@code GET  /order-items/:id} : get the "id" orderItem.
     *
     * @param id the id of the orderItem to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the orderItem, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<OrderItem>> getOrderItem(@PathVariable("id") Long id) {
        LOG.debug("REST request to get OrderItem : {}", id);
        Mono<OrderItem> orderItem = orderItemRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(orderItem);
    }

    /**
     * {@code DELETE  /order-items/:id} : delete the "id" orderItem.
     *
     * @param id the id of the orderItem to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteOrderItem(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete OrderItem : {}", id);
        return orderItemRepository
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
