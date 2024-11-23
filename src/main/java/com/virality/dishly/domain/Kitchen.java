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
 * A Kitchen.
 */
@Table("kitchen")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Kitchen implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("name")
    private String name;

    @Column("description")
    private String description;

    @Column("image")
    private String image;

    @Column("created_at")
    private Instant createdAt;

    @Column("updated_at")
    private Instant updatedAt;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "kitchen", "menu" }, allowSetters = true)
    private Set<Dish> dishes = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Kitchen id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Kitchen name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public Kitchen description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return this.image;
    }

    public Kitchen image(String image) {
        this.setImage(image);
        return this;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public Kitchen createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public Kitchen updatedAt(Instant updatedAt) {
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
            this.dishes.forEach(i -> i.setKitchen(null));
        }
        if (dishes != null) {
            dishes.forEach(i -> i.setKitchen(this));
        }
        this.dishes = dishes;
    }

    public Kitchen dishes(Set<Dish> dishes) {
        this.setDishes(dishes);
        return this;
    }

    public Kitchen addDish(Dish dish) {
        this.dishes.add(dish);
        dish.setKitchen(this);
        return this;
    }

    public Kitchen removeDish(Dish dish) {
        this.dishes.remove(dish);
        dish.setKitchen(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Kitchen)) {
            return false;
        }
        return getId() != null && getId().equals(((Kitchen) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Kitchen{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", image='" + getImage() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            "}";
    }
}
