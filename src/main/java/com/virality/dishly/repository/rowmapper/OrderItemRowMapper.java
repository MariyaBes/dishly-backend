package com.virality.dishly.repository.rowmapper;

import com.virality.dishly.domain.OrderItem;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link OrderItem}, with proper type conversions.
 */
@Service
public class OrderItemRowMapper implements BiFunction<Row, String, OrderItem> {

    private final ColumnConverter converter;

    public OrderItemRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link OrderItem} stored in the database.
     */
    @Override
    public OrderItem apply(Row row, String prefix) {
        OrderItem entity = new OrderItem();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setQuantity(converter.fromRow(row, prefix + "_quantity", Integer.class));
        entity.setPrice(converter.fromRow(row, prefix + "_price", Integer.class));
        entity.setTotalPrice(converter.fromRow(row, prefix + "_total_price", Integer.class));
        entity.setOrderId(converter.fromRow(row, prefix + "_order_id", Long.class));
        entity.setDishId(converter.fromRow(row, prefix + "_dish_id", Long.class));
        return entity;
    }
}
