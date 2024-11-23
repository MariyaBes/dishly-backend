package com.virality.dishly.repository;

import com.virality.dishly.domain.OrderItem;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the OrderItem entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OrderItemRepository extends ReactiveCrudRepository<OrderItem, Long>, OrderItemRepositoryInternal {
    @Query("SELECT * FROM order_item entity WHERE entity.order_id = :id")
    Flux<OrderItem> findByOrder(Long id);

    @Query("SELECT * FROM order_item entity WHERE entity.order_id IS NULL")
    Flux<OrderItem> findAllWhereOrderIsNull();

    @Query("SELECT * FROM order_item entity WHERE entity.dish_id = :id")
    Flux<OrderItem> findByDish(Long id);

    @Query("SELECT * FROM order_item entity WHERE entity.dish_id IS NULL")
    Flux<OrderItem> findAllWhereDishIsNull();

    @Override
    <S extends OrderItem> Mono<S> save(S entity);

    @Override
    Flux<OrderItem> findAll();

    @Override
    Mono<OrderItem> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface OrderItemRepositoryInternal {
    <S extends OrderItem> Mono<S> save(S entity);

    Flux<OrderItem> findAllBy(Pageable pageable);

    Flux<OrderItem> findAll();

    Mono<OrderItem> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<OrderItem> findAllBy(Pageable pageable, Criteria criteria);
}
