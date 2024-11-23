package com.virality.dishly.repository.rowmapper;

import com.virality.dishly.domain.Kitchen;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Kitchen}, with proper type conversions.
 */
@Service
public class KitchenRowMapper implements BiFunction<Row, String, Kitchen> {

    private final ColumnConverter converter;

    public KitchenRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Kitchen} stored in the database.
     */
    @Override
    public Kitchen apply(Row row, String prefix) {
        Kitchen entity = new Kitchen();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        entity.setImage(converter.fromRow(row, prefix + "_image", String.class));
        entity.setCreatedAt(converter.fromRow(row, prefix + "_created_at", Instant.class));
        entity.setUpdatedAt(converter.fromRow(row, prefix + "_updated_at", Instant.class));
        return entity;
    }
}
