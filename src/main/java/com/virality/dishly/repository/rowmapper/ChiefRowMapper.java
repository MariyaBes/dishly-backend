package com.virality.dishly.repository.rowmapper;

import com.virality.dishly.domain.Chief;
import com.virality.dishly.domain.enumeration.ChiefStatus;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Chief}, with proper type conversions.
 */
@Service
public class ChiefRowMapper implements BiFunction<Row, String, Chief> {

    private final ColumnConverter converter;

    public ChiefRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Chief} stored in the database.
     */
    @Override
    public Chief apply(Row row, String prefix) {
        Chief entity = new Chief();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setRating(converter.fromRow(row, prefix + "_rating", Float.class));
        entity.setChiefStatus(converter.fromRow(row, prefix + "_chief_status", ChiefStatus.class));
        entity.setAbout(converter.fromRow(row, prefix + "_about", String.class));
        entity.setAdditionalLinks(converter.fromRow(row, prefix + "_additional_links", String.class));
        entity.setEducationDocument(converter.fromRow(row, prefix + "_education_document", String.class));
        entity.setMedicalBook(converter.fromRow(row, prefix + "_medical_book", String.class));
        return entity;
    }
}
