package com.virality.dishly.repository;

import com.virality.dishly.domain.Dish;
import com.virality.dishly.repository.rowmapper.DishRowMapper;
import com.virality.dishly.repository.rowmapper.KitchenRowMapper;
import com.virality.dishly.repository.rowmapper.MenuRowMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Comparison;
import org.springframework.data.relational.core.sql.Condition;
import org.springframework.data.relational.core.sql.Conditions;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoinCondition;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC custom repository implementation for the Dish entity.
 */
@SuppressWarnings("unused")
class DishRepositoryInternalImpl extends SimpleR2dbcRepository<Dish, Long> implements DishRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final KitchenRowMapper kitchenMapper;
    private final MenuRowMapper menuMapper;
    private final DishRowMapper dishMapper;

    private static final Table entityTable = Table.aliased("dish", EntityManager.ENTITY_ALIAS);
    private static final Table kitchenTable = Table.aliased("kitchen", "kitchen");
    private static final Table menuTable = Table.aliased("menu", "menu");

    public DishRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        KitchenRowMapper kitchenMapper,
        MenuRowMapper menuMapper,
        DishRowMapper dishMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Dish.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.kitchenMapper = kitchenMapper;
        this.menuMapper = menuMapper;
        this.dishMapper = dishMapper;
    }

    @Override
    public Flux<Dish> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Dish> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = DishSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(KitchenSqlHelper.getColumns(kitchenTable, "kitchen"));
        columns.addAll(MenuSqlHelper.getColumns(menuTable, "menu"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(kitchenTable)
            .on(Column.create("kitchen_id", entityTable))
            .equals(Column.create("id", kitchenTable))
            .leftOuterJoin(menuTable)
            .on(Column.create("menu_id", entityTable))
            .equals(Column.create("id", menuTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Dish.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Dish> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Dish> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private Dish process(Row row, RowMetadata metadata) {
        Dish entity = dishMapper.apply(row, "e");
        entity.setKitchen(kitchenMapper.apply(row, "kitchen"));
        entity.setMenu(menuMapper.apply(row, "menu"));
        return entity;
    }

    @Override
    public <S extends Dish> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
