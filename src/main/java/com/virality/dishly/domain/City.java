package com.virality.dishly.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A City.
 */
@Table("city")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class City implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("city")
    private String city;

    @Column("has_object")
    private Boolean hasObject;

    @Column("created_at")
    private Instant createdAt;

    @Column("updated_at")
    private Instant updatedAt;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "city" }, allowSetters = true)
    private Set<Address> addrees = new HashSet<>();

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "city" }, allowSetters = true)
    private Set<Users> users = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public City id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCity() {
        return this.city;
    }

    public City city(String city) {
        this.setCity(city);
        return this;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Boolean getHasObject() {
        return this.hasObject;
    }

    public City hasObject(Boolean hasObject) {
        this.setHasObject(hasObject);
        return this;
    }

    public void setHasObject(Boolean hasObject) {
        this.hasObject = hasObject;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public City createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public City updatedAt(Instant updatedAt) {
        this.setUpdatedAt(updatedAt);
        return this;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Set<Address> getAddrees() {
        return this.addrees;
    }

    public void setAddrees(Set<Address> addresses) {
        if (this.addrees != null) {
            this.addrees.forEach(i -> i.setCity(null));
        }
        if (addresses != null) {
            addresses.forEach(i -> i.setCity(this));
        }
        this.addrees = addresses;
    }

    public City addrees(Set<Address> addresses) {
        this.setAddrees(addresses);
        return this;
    }

    public City addAddree(Address address) {
        this.addrees.add(address);
        address.setCity(this);
        return this;
    }

    public City removeAddree(Address address) {
        this.addrees.remove(address);
        address.setCity(null);
        return this;
    }

    public Set<Users> getUsers() {
        return this.users;
    }

    public void setUsers(Set<Users> users) {
        if (this.users != null) {
            this.users.forEach(i -> i.setCity(null));
        }
        if (users != null) {
            users.forEach(i -> i.setCity(this));
        }
        this.users = users;
    }

    public City users(Set<Users> users) {
        this.setUsers(users);
        return this;
    }

    public City addUser(Users users) {
        this.users.add(users);
        users.setCity(this);
        return this;
    }

    public City removeUser(Users users) {
        this.users.remove(users);
        users.setCity(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof City)) {
            return false;
        }
        return getId() != null && getId().equals(((City) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "City{" +
            "id=" + getId() +
            ", city='" + getCity() + "'" +
            ", hasObject='" + getHasObject() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            "}";
    }
}
