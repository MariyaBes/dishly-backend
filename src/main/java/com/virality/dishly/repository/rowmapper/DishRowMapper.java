package com.virality.dishly.repository.rowmapper;

import com.virality.dishly.domain.Dish;
import com.virality.dishly.domain.enumeration.DishStatus;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Dish}, with proper type conversions.
 */
@Service
public class DishRowMapper implements BiFunction<Row, String, Dish> {

    private final ColumnConverter converter;

    public DishRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Dish} stored in the database.
     */
    @Override
    public Dish apply(Row row, String prefix) {
        Dish entity = new Dish();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setPrice(converter.fromRow(row, prefix + "_price", Integer.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        entity.setPreparationTime(converter.fromRow(row, prefix + "_preparation_time", Integer.class));
        entity.setImage(converter.fromRow(row, prefix + "_image", String.class));
        entity.setStatus(converter.fromRow(row, prefix + "_status", String.class));
        entity.setCreatedAt(converter.fromRow(row, prefix + "_created_at", Instant.class));
        entity.setUpdatedAt(converter.fromRow(row, prefix + "_updated_at", Instant.class));
        entity.setComposition(converter.fromRow(row, prefix + "_composition", String.class));
        entity.setWeight(converter.fromRow(row, prefix + "_weight", Integer.class));
        entity.setDishStatus(converter.fromRow(row, prefix + "_dish_status", DishStatus.class));
        entity.setKitchenId(converter.fromRow(row, prefix + "_kitchen_id", Long.class));
        entity.setMenuId(converter.fromRow(row, prefix + "_menu_id", Long.class));
        return entity;
    }
}
