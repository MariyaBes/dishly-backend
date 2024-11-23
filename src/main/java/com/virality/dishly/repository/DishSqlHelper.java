package com.virality.dishly.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class DishSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("name", table, columnPrefix + "_name"));
        columns.add(Column.aliased("price", table, columnPrefix + "_price"));
        columns.add(Column.aliased("description", table, columnPrefix + "_description"));
        columns.add(Column.aliased("preparation_time", table, columnPrefix + "_preparation_time"));
        columns.add(Column.aliased("image", table, columnPrefix + "_image"));
        columns.add(Column.aliased("status", table, columnPrefix + "_status"));
        columns.add(Column.aliased("created_at", table, columnPrefix + "_created_at"));
        columns.add(Column.aliased("updated_at", table, columnPrefix + "_updated_at"));
        columns.add(Column.aliased("composition", table, columnPrefix + "_composition"));
        columns.add(Column.aliased("weight", table, columnPrefix + "_weight"));
        columns.add(Column.aliased("dish_status", table, columnPrefix + "_dish_status"));

        columns.add(Column.aliased("kitchen_id", table, columnPrefix + "_kitchen_id"));
        columns.add(Column.aliased("menu_id", table, columnPrefix + "_menu_id"));
        return columns;
    }
}
