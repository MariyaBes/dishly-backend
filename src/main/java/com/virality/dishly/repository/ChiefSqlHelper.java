package com.virality.dishly.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class ChiefSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("rating", table, columnPrefix + "_rating"));
        columns.add(Column.aliased("chief_status", table, columnPrefix + "_chief_status"));
        columns.add(Column.aliased("about", table, columnPrefix + "_about"));
        columns.add(Column.aliased("additional_links", table, columnPrefix + "_additional_links"));
        columns.add(Column.aliased("education_document", table, columnPrefix + "_education_document"));
        columns.add(Column.aliased("medical_book", table, columnPrefix + "_medical_book"));

        return columns;
    }
}
