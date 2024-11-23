package com.virality.dishly.repository.rowmapper;

import com.virality.dishly.domain.Users;
import com.virality.dishly.domain.enumeration.Gender;
import com.virality.dishly.domain.enumeration.Role;
import com.virality.dishly.domain.enumeration.UserStatus;
import com.virality.dishly.domain.enumeration.VerificationStatus;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Users}, with proper type conversions.
 */
@Service
public class UsersRowMapper implements BiFunction<Row, String, Users> {

    private final ColumnConverter converter;

    public UsersRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Users} stored in the database.
     */
    @Override
    public Users apply(Row row, String prefix) {
        Users entity = new Users();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setUsername(converter.fromRow(row, prefix + "_username", String.class));
        entity.setFirstName(converter.fromRow(row, prefix + "_first_name", String.class));
        entity.setLastName(converter.fromRow(row, prefix + "_last_name", String.class));
        entity.setEmail(converter.fromRow(row, prefix + "_email", String.class));
        entity.setPhone(converter.fromRow(row, prefix + "_phone", String.class));
        entity.setPasswordHash(converter.fromRow(row, prefix + "_password_hash", String.class));
        entity.setImage(converter.fromRow(row, prefix + "_image", String.class));
        entity.setStatus(converter.fromRow(row, prefix + "_status", String.class));
        entity.setGender(converter.fromRow(row, prefix + "_gender", Gender.class));
        entity.setRole(converter.fromRow(row, prefix + "_role", Role.class));
        entity.setVerificationStatus(converter.fromRow(row, prefix + "_verification_status", VerificationStatus.class));
        entity.setCreatedAt(converter.fromRow(row, prefix + "_created_at", Instant.class));
        entity.setUpdatedAt(converter.fromRow(row, prefix + "_updated_at", Instant.class));
        entity.setUserStatus(converter.fromRow(row, prefix + "_user_status", UserStatus.class));
        entity.setCityId(converter.fromRow(row, prefix + "_city_id", Long.class));
        return entity;
    }
}
