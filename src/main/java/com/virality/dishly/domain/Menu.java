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
 * A Menu.
 */
@Table("menu")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Menu implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("name")
    private String name;

    @Column("description")
    private String description;

    @Column("created_at")
    private Instant createdAt;

    @Column("updated_at")
    private Instant updatedAt;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "kitchen", "menu" }, allowSetters = true)
    private Set<Dish> dishes = new HashSet<>();

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "menus", "chiefLinks" }, allowSetters = true)
    private Chief chief;

    @Column("chief_id")
    private Long chiefId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Menu id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Menu name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public Menu description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public Menu createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public Menu updatedAt(Instant updatedAt) {
        this.setUpdatedAt(updatedAt);
        return this;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Set<Dish> getDishes() {
        return this.dishes;
    }

    public void setDishes(Set<Dish> dishes) {
        if (this.dishes != null) {
            this.dishes.forEach(i -> i.setMenu(null));
        }
        if (dishes != null) {
            dishes.forEach(i -> i.setMenu(this));
        }
        this.dishes = dishes;
    }

    public Menu dishes(Set<Dish> dishes) {
        this.setDishes(dishes);
        return this;
    }

    public Menu addDish(Dish dish) {
        this.dishes.add(dish);
        dish.setMenu(this);
        return this;
    }

    public Menu removeDish(Dish dish) {
        this.dishes.remove(dish);
        dish.setMenu(null);
        return this;
    }

    public Chief getChief() {
        return this.chief;
    }

    public void setChief(Chief chief) {
        this.chief = chief;
        this.chiefId = chief != null ? chief.getId() : null;
    }

    public Menu chief(Chief chief) {
        this.setChief(chief);
        return this;
    }

    public Long getChiefId() {
        return this.chiefId;
    }

    public void setChiefId(Long chief) {
        this.chiefId = chief;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Menu)) {
            return false;
        }
        return getId() != null && getId().equals(((Menu) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Menu{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            "}";
    }
}
