package com.virality.dishly.repository.rowmapper;

import com.virality.dishly.domain.Menu;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Menu}, with proper type conversions.
 */
@Service
public class MenuRowMapper implements BiFunction<Row, String, Menu> {

    private final ColumnConverter converter;

    public MenuRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Menu} stored in the database.
     */
    @Override
    public Menu apply(Row row, String prefix) {
        Menu entity = new Menu();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        entity.setCreatedAt(converter.fromRow(row, prefix + "_created_at", Instant.class));
        entity.setUpdatedAt(converter.fromRow(row, prefix + "_updated_at", Instant.class));
        entity.setChiefId(converter.fromRow(row, prefix + "_chief_id", Long.class));
        return entity;
    }
}
