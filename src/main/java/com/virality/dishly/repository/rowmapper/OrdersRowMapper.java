package com.virality.dishly.repository.rowmapper;

import com.virality.dishly.domain.Orders;
import com.virality.dishly.domain.enumeration.OrderStatus;
import com.virality.dishly.domain.enumeration.PaymentMethod;
import com.virality.dishly.domain.enumeration.PaymentStatus;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Orders}, with proper type conversions.
 */
@Service
public class OrdersRowMapper implements BiFunction<Row, String, Orders> {

    private final ColumnConverter converter;

    public OrdersRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Orders} stored in the database.
     */
    @Override
    public Orders apply(Row row, String prefix) {
        Orders entity = new Orders();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setStatus(converter.fromRow(row, prefix + "_status", String.class));
        entity.setUpdatedAt(converter.fromRow(row, prefix + "_updated_at", Instant.class));
        entity.setCreatedAt(converter.fromRow(row, prefix + "_created_at", Instant.class));
        entity.setSum(converter.fromRow(row, prefix + "_sum", Long.class));
        entity.setPaymentMethod(converter.fromRow(row, prefix + "_payment_method", PaymentMethod.class));
        entity.setPaymentStatus(converter.fromRow(row, prefix + "_payment_status", PaymentStatus.class));
        entity.setTransactionId(converter.fromRow(row, prefix + "_transaction_id", Long.class));
        entity.setOrderStatus(converter.fromRow(row, prefix + "_order_status", OrderStatus.class));
        entity.setUserId(converter.fromRow(row, prefix + "_user_id", Long.class));
        entity.setChiefId(converter.fromRow(row, prefix + "_chief_id", Long.class));
        entity.setCityId(converter.fromRow(row, prefix + "_city_id", Long.class));
        return entity;
    }
}
