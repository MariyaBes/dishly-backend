package com.virality.dishly.repository.rowmapper;

import com.virality.dishly.domain.ChiefLinks;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link ChiefLinks}, with proper type conversions.
 */
@Service
public class ChiefLinksRowMapper implements BiFunction<Row, String, ChiefLinks> {

    private final ColumnConverter converter;

    public ChiefLinksRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link ChiefLinks} stored in the database.
     */
    @Override
    public ChiefLinks apply(Row row, String prefix) {
        ChiefLinks entity = new ChiefLinks();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setTelegramLink(converter.fromRow(row, prefix + "_telegram_link", String.class));
        entity.setVkLink(converter.fromRow(row, prefix + "_vk_link", String.class));
        entity.setOdnoklassnikiLink(converter.fromRow(row, prefix + "_odnoklassniki_link", String.class));
        entity.setYoutubeLink(converter.fromRow(row, prefix + "_youtube_link", String.class));
        entity.setRutubeLink(converter.fromRow(row, prefix + "_rutube_link", String.class));
        entity.setChiefId(converter.fromRow(row, prefix + "_chief_id", Long.class));
        return entity;
    }
}
