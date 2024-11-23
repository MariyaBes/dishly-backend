package com.virality.dishly.repository;

import com.virality.dishly.domain.Menu;
import com.virality.dishly.repository.rowmapper.ChiefRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the Menu entity.
 */
@SuppressWarnings("unused")
class MenuRepositoryInternalImpl extends SimpleR2dbcRepository<Menu, Long> implements MenuRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final ChiefRowMapper chiefMapper;
    private final MenuRowMapper menuMapper;

    private static final Table entityTable = Table.aliased("menu", EntityManager.ENTITY_ALIAS);
    private static final Table chiefTable = Table.aliased("chief", "chief");

    public MenuRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        ChiefRowMapper chiefMapper,
        MenuRowMapper menuMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Menu.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.chiefMapper = chiefMapper;
        this.menuMapper = menuMapper;
    }

    @Override
    public Flux<Menu> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Menu> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = MenuSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(ChiefSqlHelper.getColumns(chiefTable, "chief"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(chiefTable)
            .on(Column.create("chief_id", entityTable))
            .equals(Column.create("id", chiefTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Menu.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Menu> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Menu> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private Menu process(Row row, RowMetadata metadata) {
        Menu entity = menuMapper.apply(row, "e");
        entity.setChief(chiefMapper.apply(row, "chief"));
        return entity;
    }

    @Override
    public <S extends Menu> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
