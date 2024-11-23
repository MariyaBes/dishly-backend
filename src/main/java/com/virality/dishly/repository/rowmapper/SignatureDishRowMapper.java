package com.virality.dishly.repository.rowmapper;

import com.virality.dishly.domain.SignatureDish;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link SignatureDish}, with proper type conversions.
 */
@Service
public class SignatureDishRowMapper implements BiFunction<Row, String, SignatureDish> {

    private final ColumnConverter converter;

    public SignatureDishRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link SignatureDish} stored in the database.
     */
    @Override
    public SignatureDish apply(Row row, String prefix) {
        SignatureDish entity = new SignatureDish();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setImage(converter.fromRow(row, prefix + "_image", String.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        entity.setChiefId(converter.fromRow(row, prefix + "_chief_id", Long.class));
        return entity;
    }
}
