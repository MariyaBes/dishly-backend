package com.virality.dishly.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.virality.dishly.domain.enumeration.OrderStatus;
import com.virality.dishly.domain.enumeration.PaymentMethod;
import com.virality.dishly.domain.enumeration.PaymentStatus;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Orders.
 */
@Table("orders")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Orders implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("status")
    private String status;

    @Column("updated_at")
    private Instant updatedAt;

    @NotNull(message = "must not be null")
    @Column("created_at")
    private Instant createdAt;

    @Column("sum")
    private Long sum;

    @Column("payment_method")
    private PaymentMethod paymentMethod;

    @Column("payment_status")
    private PaymentStatus paymentStatus;

    @Column("transaction_id")
    private Long transactionId;

    @Column("order_status")
    private OrderStatus orderStatus;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "city" }, allowSetters = true)
    private Users user;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "menus", "chiefLinks" }, allowSetters = true)
    private Chief chief;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "addrees", "users" }, allowSetters = true)
    private City city;

    @Column("user_id")
    private Long userId;

    @Column("chief_id")
    private Long chiefId;

    @Column("city_id")
    private Long cityId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Orders id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatus() {
        return this.status;
    }

    public Orders status(String status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public Orders updatedAt(Instant updatedAt) {
        this.setUpdatedAt(updatedAt);
        return this;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public Orders createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Long getSum() {
        return this.sum;
    }

    public Orders sum(Long sum) {
        this.setSum(sum);
        return this;
    }

    public void setSum(Long sum) {
        this.sum = sum;
    }

    public PaymentMethod getPaymentMethod() {
        return this.paymentMethod;
    }

    public Orders paymentMethod(PaymentMethod paymentMethod) {
        this.setPaymentMethod(paymentMethod);
        return this;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public PaymentStatus getPaymentStatus() {
        return this.paymentStatus;
    }

    public Orders paymentStatus(PaymentStatus paymentStatus) {
        this.setPaymentStatus(paymentStatus);
        return this;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public Long getTransactionId() {
        return this.transactionId;
    }

    public Orders transactionId(Long transactionId) {
        this.setTransactionId(transactionId);
        return this;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public OrderStatus getOrderStatus() {
        return this.orderStatus;
    }

    public Orders orderStatus(OrderStatus orderStatus) {
        this.setOrderStatus(orderStatus);
        return this;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Users getUser() {
        return this.user;
    }

    public void setUser(Users users) {
        this.user = users;
        this.userId = users != null ? users.getId() : null;
    }

    public Orders user(Users users) {
        this.setUser(users);
        return this;
    }

    public Chief getChief() {
        return this.chief;
    }

    public void setChief(Chief chief) {
        this.chief = chief;
        this.chiefId = chief != null ? chief.getId() : null;
    }

    public Orders chief(Chief chief) {
        this.setChief(chief);
        return this;
    }

    public City getCity() {
        return this.city;
    }

    public void setCity(City city) {
        this.city = city;
        this.cityId = city != null ? city.getId() : null;
    }

    public Orders city(City city) {
        this.setCity(city);
        return this;
    }

    public Long getUserId() {
        return this.userId;
    }

    public void setUserId(Long users) {
        this.userId = users;
    }

    public Long getChiefId() {
        return this.chiefId;
    }

    public void setChiefId(Long chief) {
        this.chiefId = chief;
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
        if (!(o instanceof Orders)) {
            return false;
        }
        return getId() != null && getId().equals(((Orders) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Orders{" +
            "id=" + getId() +
            ", status='" + getStatus() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", sum=" + getSum() +
            ", paymentMethod='" + getPaymentMethod() + "'" +
            ", paymentStatus='" + getPaymentStatus() + "'" +
            ", transactionId=" + getTransactionId() +
            ", orderStatus='" + getOrderStatus() + "'" +
            "}";
    }
}
