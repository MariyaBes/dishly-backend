package com.virality.dishly.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A OrderItem.
 */
@Table("order_item")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class OrderItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("quantity")
    private Integer quantity;

    @Column("price")
    private Integer price;

    @Column("total_price")
    private Integer totalPrice;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "user", "chief", "city" }, allowSetters = true)
    private Orders order;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "kitchen", "menu" }, allowSetters = true)
    private Dish dish;

    @Column("order_id")
    private Long orderId;

    @Column("dish_id")
    private Long dishId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public OrderItem id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQuantity() {
        return this.quantity;
    }

    public OrderItem quantity(Integer quantity) {
        this.setQuantity(quantity);
        return this;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getPrice() {
        return this.price;
    }

    public OrderItem price(Integer price) {
        this.setPrice(price);
        return this;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getTotalPrice() {
        return this.totalPrice;
    }

    public OrderItem totalPrice(Integer totalPrice) {
        this.setTotalPrice(totalPrice);
        return this;
    }

    public void setTotalPrice(Integer totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Orders getOrder() {
        return this.order;
    }

    public void setOrder(Orders orders) {
        this.order = orders;
        this.orderId = orders != null ? orders.getId() : null;
    }

    public OrderItem order(Orders orders) {
        this.setOrder(orders);
        return this;
    }

    public Dish getDish() {
        return this.dish;
    }

    public void setDish(Dish dish) {
        this.dish = dish;
        this.dishId = dish != null ? dish.getId() : null;
    }

    public OrderItem dish(Dish dish) {
        this.setDish(dish);
        return this;
    }

    public Long getOrderId() {
        return this.orderId;
    }

    public void setOrderId(Long orders) {
        this.orderId = orders;
    }

    public Long getDishId() {
        return this.dishId;
    }

    public void setDishId(Long dish) {
        this.dishId = dish;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OrderItem)) {
            return false;
        }
        return getId() != null && getId().equals(((OrderItem) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OrderItem{" +
            "id=" + getId() +
            ", quantity=" + getQuantity() +
            ", price=" + getPrice() +
            ", totalPrice=" + getTotalPrice() +
            "}";
    }
}
