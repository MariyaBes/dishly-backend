package com.virality.dishly.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class UsersSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("username", table, columnPrefix + "_username"));
        columns.add(Column.aliased("first_name", table, columnPrefix + "_first_name"));
        columns.add(Column.aliased("last_name", table, columnPrefix + "_last_name"));
        columns.add(Column.aliased("email", table, columnPrefix + "_email"));
        columns.add(Column.aliased("phone", table, columnPrefix + "_phone"));
        columns.add(Column.aliased("password_hash", table, columnPrefix + "_password_hash"));
        columns.add(Column.aliased("image", table, columnPrefix + "_image"));
        columns.add(Column.aliased("status", table, columnPrefix + "_status"));
        columns.add(Column.aliased("gender", table, columnPrefix + "_gender"));
        columns.add(Column.aliased("role", table, columnPrefix + "_role"));
        columns.add(Column.aliased("verification_status", table, columnPrefix + "_verification_status"));
        columns.add(Column.aliased("created_at", table, columnPrefix + "_created_at"));
        columns.add(Column.aliased("updated_at", table, columnPrefix + "_updated_at"));
        columns.add(Column.aliased("user_status", table, columnPrefix + "_user_status"));

        columns.add(Column.aliased("city_id", table, columnPrefix + "_city_id"));
        return columns;
    }
}
