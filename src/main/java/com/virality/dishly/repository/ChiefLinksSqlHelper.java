package com.virality.dishly.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class ChiefLinksSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("telegram_link", table, columnPrefix + "_telegram_link"));
        columns.add(Column.aliased("vk_link", table, columnPrefix + "_vk_link"));
        columns.add(Column.aliased("odnoklassniki_link", table, columnPrefix + "_odnoklassniki_link"));
        columns.add(Column.aliased("youtube_link", table, columnPrefix + "_youtube_link"));
        columns.add(Column.aliased("rutube_link", table, columnPrefix + "_rutube_link"));

        columns.add(Column.aliased("chief_id", table, columnPrefix + "_chief_id"));
        return columns;
    }
}
