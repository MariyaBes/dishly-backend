package com.virality.dishly.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class AddressSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("address", table, columnPrefix + "_address"));
        columns.add(Column.aliased("ymap_y", table, columnPrefix + "_ymap_y"));
        columns.add(Column.aliased("ymap_x", table, columnPrefix + "_ymap_x"));
        columns.add(Column.aliased("created_at", table, columnPrefix + "_created_at"));
        columns.add(Column.aliased("updated_at", table, columnPrefix + "_updated_at"));

        columns.add(Column.aliased("city_id", table, columnPrefix + "_city_id"));
        return columns;
    }
}
