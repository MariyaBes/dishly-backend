package com.virality.dishly.repository.rowmapper;

import com.virality.dishly.domain.Address;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Address}, with proper type conversions.
 */
@Service
public class AddressRowMapper implements BiFunction<Row, String, Address> {

    private final ColumnConverter converter;

    public AddressRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Address} stored in the database.
     */
    @Override
    public Address apply(Row row, String prefix) {
        Address entity = new Address();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setAddress(converter.fromRow(row, prefix + "_address", String.class));
        entity.setYmapY(converter.fromRow(row, prefix + "_ymap_y", String.class));
        entity.setYmapX(converter.fromRow(row, prefix + "_ymap_x", String.class));
        entity.setCreatedAt(converter.fromRow(row, prefix + "_created_at", Instant.class));
        entity.setUpdatedAt(converter.fromRow(row, prefix + "_updated_at", Instant.class));
        entity.setCityId(converter.fromRow(row, prefix + "_city_id", Long.class));
        return entity;
    }
}
