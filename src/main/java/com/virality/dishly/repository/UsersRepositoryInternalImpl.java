package com.virality.dishly.repository;

import com.virality.dishly.domain.Users;
import com.virality.dishly.repository.rowmapper.CityRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the Users entity.
 */
@SuppressWarnings("unused")
class UsersRepositoryInternalImpl extends SimpleR2dbcRepository<Users, Long> implements UsersRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final CityRowMapper cityMapper;
    private final UsersRowMapper usersMapper;

    private static final Table entityTable = Table.aliased("users", EntityManager.ENTITY_ALIAS);
    private static final Table cityTable = Table.aliased("city", "city");

    public UsersRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        CityRowMapper cityMapper,
        UsersRowMapper usersMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Users.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.cityMapper = cityMapper;
        this.usersMapper = usersMapper;
    }

    @Override
    public Flux<Users> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Users> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = UsersSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(CitySqlHelper.getColumns(cityTable, "city"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(cityTable)
            .on(Column.create("city_id", entityTable))
            .equals(Column.create("id", cityTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Users.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Users> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Users> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private Users process(Row row, RowMetadata metadata) {
        Users entity = usersMapper.apply(row, "e");
        entity.setCity(cityMapper.apply(row, "city"));
        return entity;
    }

    @Override
    public <S extends Users> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
