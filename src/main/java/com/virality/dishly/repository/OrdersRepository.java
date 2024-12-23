package com.virality.dishly.repository;

import com.virality.dishly.domain.Orders;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Orders entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OrdersRepository extends ReactiveCrudRepository<Orders, Long>, OrdersRepositoryInternal {
    @Query("SELECT * FROM orders entity WHERE entity.user_id = :id")
    Flux<Orders> findByUser(Long id);

    @Query("SELECT * FROM orders entity WHERE entity.user_id IS NULL")
    Flux<Orders> findAllWhereUserIsNull();

    @Query("SELECT * FROM orders entity WHERE entity.chief_id = :id")
    Flux<Orders> findByChief(Long id);

    @Query("SELECT * FROM orders entity WHERE entity.chief_id IS NULL")
    Flux<Orders> findAllWhereChiefIsNull();

    @Query("SELECT * FROM orders entity WHERE entity.city_id = :id")
    Flux<Orders> findByCity(Long id);

    @Query("SELECT * FROM orders entity WHERE entity.city_id IS NULL")
    Flux<Orders> findAllWhereCityIsNull();

    @Override
    <S extends Orders> Mono<S> save(S entity);

    @Override
    Flux<Orders> findAll();

    @Override
    Mono<Orders> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface OrdersRepositoryInternal {
    <S extends Orders> Mono<S> save(S entity);

    Flux<Orders> findAllBy(Pageable pageable);

    Flux<Orders> findAll();

    Mono<Orders> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Orders> findAllBy(Pageable pageable, Criteria criteria);
}
