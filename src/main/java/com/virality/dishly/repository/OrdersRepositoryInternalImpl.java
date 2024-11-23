package com.virality.dishly.repository;

import com.virality.dishly.domain.Orders;
import com.virality.dishly.repository.rowmapper.ChiefRowMapper;
import com.virality.dishly.repository.rowmapper.CityRowMapper;
import com.virality.dishly.repository.rowmapper.OrdersRowMapper;
import com.virality.dishly.repository.rowmapper.UsersRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the Orders entity.
 */
@SuppressWarnings("unused")
class OrdersRepositoryInternalImpl extends SimpleR2dbcRepository<Orders, Long> implements OrdersRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final UsersRowMapper usersMapper;
    private final ChiefRowMapper chiefMapper;
    private final CityRowMapper cityMapper;
    private final OrdersRowMapper ordersMapper;

    private static final Table entityTable = Table.aliased("orders", EntityManager.ENTITY_ALIAS);
    private static final Table userTable = Table.aliased("users", "e_user");
    private static final Table chiefTable = Table.aliased("chief", "chief");
    private static final Table cityTable = Table.aliased("city", "city");

    public OrdersRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        UsersRowMapper usersMapper,
        ChiefRowMapper chiefMapper,
        CityRowMapper cityMapper,
        OrdersRowMapper ordersMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Orders.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.usersMapper = usersMapper;
        this.chiefMapper = chiefMapper;
        this.cityMapper = cityMapper;
        this.ordersMapper = ordersMapper;
    }

    @Override
    public Flux<Orders> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Orders> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = OrdersSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(UsersSqlHelper.getColumns(userTable, "user"));
        columns.addAll(ChiefSqlHelper.getColumns(chiefTable, "chief"));
        columns.addAll(CitySqlHelper.getColumns(cityTable, "city"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(userTable)
            .on(Column.create("user_id", entityTable))
            .equals(Column.create("id", userTable))
            .leftOuterJoin(chiefTable)
            .on(Column.create("chief_id", entityTable))
            .equals(Column.create("id", chiefTable))
            .leftOuterJoin(cityTable)
            .on(Column.create("city_id", entityTable))
            .equals(Column.create("id", cityTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Orders.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Orders> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Orders> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private Orders process(Row row, RowMetadata metadata) {
        Orders entity = ordersMapper.apply(row, "e");
        entity.setUser(usersMapper.apply(row, "user"));
        entity.setChief(chiefMapper.apply(row, "chief"));
        entity.setCity(cityMapper.apply(row, "city"));
        return entity;
    }

    @Override
    public <S extends Orders> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
