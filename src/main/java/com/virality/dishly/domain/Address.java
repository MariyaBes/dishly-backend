package com.virality.dishly.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Address.
 */
@Table("address")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Address implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("address")
    private String address;

    @Column("ymap_y")
    private String ymapY;

    @Column("ymap_x")
    private String ymapX;

    @Column("created_at")
    private Instant createdAt;

    @Column("updated_at")
    private Instant updatedAt;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "addrees", "users" }, allowSetters = true)
    private City city;

    @Column("city_id")
    private Long cityId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Address id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAddress() {
        return this.address;
    }

    public Address address(String address) {
        this.setAddress(address);
        return this;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getYmapY() {
        return this.ymapY;
    }

    public Address ymapY(String ymapY) {
        this.setYmapY(ymapY);
        return this;
    }

    public void setYmapY(String ymapY) {
        this.ymapY = ymapY;
    }

    public String getYmapX() {
        return this.ymapX;
    }

    public Address ymapX(String ymapX) {
        this.setYmapX(ymapX);
        return this;
    }

    public void setYmapX(String ymapX) {
        this.ymapX = ymapX;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public Address createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public Address updatedAt(Instant updatedAt) {
        this.setUpdatedAt(updatedAt);
        return this;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public City getCity() {
        return this.city;
    }

    public void setCity(City city) {
        this.city = city;
        this.cityId = city != null ? city.getId() : null;
    }

    public Address city(City city) {
        this.setCity(city);
        return this;
    }

    public Long getCityId() {
        return this.cityId;
    }

    public void setCityId(Long city) {
        this.cityId = city;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Address)) {
            return false;
        }
        return getId() != null && getId().equals(((Address) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Address{" +
            "id=" + getId() +
            ", address='" + getAddress() + "'" +
            ", ymapY='" + getYmapY() + "'" +
            ", ymapX='" + getYmapX() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            "}";
    }
}
