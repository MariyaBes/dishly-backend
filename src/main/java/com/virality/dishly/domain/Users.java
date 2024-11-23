package com.virality.dishly.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.virality.dishly.domain.enumeration.Gender;
import com.virality.dishly.domain.enumeration.Role;
import com.virality.dishly.domain.enumeration.UserStatus;
import com.virality.dishly.domain.enumeration.VerificationStatus;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Users.
 */
@Table("users")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Users implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Size(min = 3)
    @Column("username")
    private String username;

    @Column("first_name")
    private String firstName;

    @Column("last_name")
    private String lastName;

    @NotNull(message = "must not be null")
    @Column("email")
    private String email;

    @Column("phone")
    private String phone;

    @NotNull(message = "must not be null")
    @Column("password_hash")
    private String passwordHash;

    @Column("image")
    private String image;

    @Column("status")
    private String status;

    @Column("gender")
    private Gender gender;

    @Column("role")
    private Role role;

    @Column("verification_status")
    private VerificationStatus verificationStatus;

    @Column("created_at")
    private Instant createdAt;

    @Column("updated_at")
    private Instant updatedAt;

    @Column("user_status")
    private UserStatus userStatus;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "addrees", "users" }, allowSetters = true)
    private City city;

    @Column("city_id")
    private Long cityId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Users id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return this.username;
    }

    public Users username(String username) {
        this.setUsername(username);
        return this;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public Users firstName(String firstName) {
        this.setFirstName(firstName);
        return this;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public Users lastName(String lastName) {
        this.setLastName(lastName);
        return this;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return this.email;
    }

    public Users email(String email) {
        this.setEmail(email);
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return this.phone;
    }

    public Users phone(String phone) {
        this.setPhone(phone);
        return this;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPasswordHash() {
        return this.passwordHash;
    }

    public Users passwordHash(String passwordHash) {
        this.setPasswordHash(passwordHash);
        return this;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getImage() {
        return this.image;
    }

    public Users image(String image) {
        this.setImage(image);
        return this;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStatus() {
        return this.status;
    }

    public Users status(String status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Gender getGender() {
        return this.gender;
    }

    public Users gender(Gender gender) {
        this.setGender(gender);
        return this;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Role getRole() {
        return this.role;
    }

    public Users role(Role role) {
        this.setRole(role);
        return this;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public VerificationStatus getVerificationStatus() {
        return this.verificationStatus;
    }

    public Users verificationStatus(VerificationStatus verificationStatus) {
        this.setVerificationStatus(verificationStatus);
        return this;
    }

    public void setVerificationStatus(VerificationStatus verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public Users createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public Users updatedAt(Instant updatedAt) {
        this.setUpdatedAt(updatedAt);
        return this;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public UserStatus getUserStatus() {
        return this.userStatus;
    }

    public Users userStatus(UserStatus userStatus) {
        this.setUserStatus(userStatus);
        return this;
    }

    public void setUserStatus(UserStatus userStatus) {
        this.userStatus = userStatus;
    }

    public City getCity() {
        return this.city;
    }

    public void setCity(City city) {
        this.city = city;
        this.cityId = city != null ? city.getId() : null;
    }

    public Users city(City city) {
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
        if (!(o instanceof Users)) {
            return false;
        }
        return getId() != null && getId().equals(((Users) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Users{" +
            "id=" + getId() +
            ", username='" + getUsername() + "'" +
            ", firstName='" + getFirstName() + "'" +
            ", lastName='" + getLastName() + "'" +
            ", email='" + getEmail() + "'" +
            ", phone='" + getPhone() + "'" +
            ", passwordHash='" + getPasswordHash() + "'" +
            ", image='" + getImage() + "'" +
            ", status='" + getStatus() + "'" +
            ", gender='" + getGender() + "'" +
            ", role='" + getRole() + "'" +
            ", verificationStatus='" + getVerificationStatus() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            ", userStatus='" + getUserStatus() + "'" +
            "}";
    }
}
