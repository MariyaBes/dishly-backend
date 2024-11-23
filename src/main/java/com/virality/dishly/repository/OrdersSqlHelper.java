package com.virality.dishly.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class OrdersSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("status", table, columnPrefix + "_status"));
        columns.add(Column.aliased("updated_at", table, columnPrefix + "_updated_at"));
        columns.add(Column.aliased("created_at", table, columnPrefix + "_created_at"));
        columns.add(Column.aliased("sum", table, columnPrefix + "_sum"));
        columns.add(Column.aliased("payment_method", table, columnPrefix + "_payment_method"));
        columns.add(Column.aliased("payment_status", table, columnPrefix + "_payment_status"));
        columns.add(Column.aliased("transaction_id", table, columnPrefix + "_transaction_id"));
        columns.add(Column.aliased("order_status", table, columnPrefix + "_order_status"));

        columns.add(Column.aliased("user_id", table, columnPrefix + "_user_id"));
        columns.add(Column.aliased("chief_id", table, columnPrefix + "_chief_id"));
        columns.add(Column.aliased("city_id", table, columnPrefix + "_city_id"));
        return columns;
    }
}
