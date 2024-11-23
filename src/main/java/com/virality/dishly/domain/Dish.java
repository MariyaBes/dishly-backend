package com.virality.dishly.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.virality.dishly.domain.enumeration.DishStatus;
import java.io.Serializable;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Dish.
 */
@Table("dish")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Dish implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("name")
    private String name;

    @Column("price")
    private Integer price;

    @Column("description")
    private String description;

    @Column("preparation_time")
    private Integer preparationTime;

    @Column("image")
    private String image;

    @Column("status")
    private String status;

    @Column("created_at")
    private Instant createdAt;

    @Column("updated_at")
    private Instant updatedAt;

    @Column("composition")
    private String composition;

    @Column("weight")
    private Integer weight;

    @Column("dish_status")
    private DishStatus dishStatus;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "dishes" }, allowSetters = true)
    private Kitchen kitchen;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "dishes", "chief" }, allowSetters = true)
    private Menu menu;

    @Column("kitchen_id")
    private Long kitchenId;

    @Column("menu_id")
    private Long menuId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Dish id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Dish name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPrice() {
        return this.price;
    }

    public Dish price(Integer price) {
        this.setPrice(price);
        return this;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getDescription() {
        return this.description;
    }

    public Dish description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPreparationTime() {
        return this.preparationTime;
    }

    public Dish preparationTime(Integer preparationTime) {
        this.setPreparationTime(preparationTime);
        return this;
    }

    public void setPreparationTime(Integer preparationTime) {
        this.preparationTime = preparationTime;
    }

    public String getImage() {
        return this.image;
    }

    public Dish image(String image) {
        this.setImage(image);
        return this;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStatus() {
        return this.status;
    }

    public Dish status(String status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public Dish createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public Dish updatedAt(Instant updatedAt) {
        this.setUpdatedAt(updatedAt);
        return this;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getComposition() {
        return this.composition;
    }

    public Dish composition(String composition) {
        this.setComposition(composition);
        return this;
    }

    public void setComposition(String composition) {
        this.composition = composition;
    }

    public Integer getWeight() {
        return this.weight;
    }

    public Dish weight(Integer weight) {
        this.setWeight(weight);
        return this;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public DishStatus getDishStatus() {
        return this.dishStatus;
    }

    public Dish dishStatus(DishStatus dishStatus) {
        this.setDishStatus(dishStatus);
        return this;
    }

    public void setDishStatus(DishStatus dishStatus) {
        this.dishStatus = dishStatus;
    }

    public Kitchen getKitchen() {
        return this.kitchen;
    }

    public void setKitchen(Kitchen kitchen) {
        this.kitchen = kitchen;
        this.kitchenId = kitchen != null ? kitchen.getId() : null;
    }

    public Dish kitchen(Kitchen kitchen) {
        this.setKitchen(kitchen);
        return this;
    }

    public Menu getMenu() {
        return this.menu;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
        this.menuId = menu != null ? menu.getId() : null;
    }

    public Dish menu(Menu menu) {
        this.setMenu(menu);
        return this;
    }

    public Long getKitchenId() {
        return this.kitchenId;
    }

    public void setKitchenId(Long kitchen) {
        this.kitchenId = kitchen;
    }

    public Long getMenuId() {
        return this.menuId;
    }

    public void setMenuId(Long menu) {
        this.menuId = menu;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Dish)) {
            return false;
        }
        return getId() != null && getId().equals(((Dish) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Dish{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", price=" + getPrice() +
            ", description='" + getDescription() + "'" +
            ", preparationTime=" + getPreparationTime() +
            ", image='" + getImage() + "'" +
            ", status='" + getStatus() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            ", composition='" + getComposition() + "'" +
            ", weight=" + getWeight() +
            ", dishStatus='" + getDishStatus() + "'" +
            "}";
    }
}
