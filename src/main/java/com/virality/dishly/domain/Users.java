package com.virality.dishly.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.virality.dishly.config.Constants;
import com.virality.dishly.domain.enumeration.Gender;
import com.virality.dishly.domain.enumeration.Role;
import com.virality.dishly.domain.enumeration.UserStatus;
import com.virality.dishly.domain.enumeration.VerificationStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 * A Users.
 */
@Table("users")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Users extends AbstractAuditingEntity<Long> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Size(min = 3, max = 50)
    @Pattern(regexp = Constants.LOGIN_REGEX)
    @Column("username")
    private String username;

    @Size(max = 15)
    @Column("first_name")
    private String firstName;

    @Size(max = 30)
    @Column("last_name")
    private String lastName;

    @Email
    @Size(min = 5, max = 254)
    @NotNull(message = "must not be null")
    @Column("email")
    private String email;

    @Size(min = 12, max = 12)
    @Column("phone")
    private String phone;

    @JsonIgnore
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
    @Enumerated(EnumType.STRING)
    private Role role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Column("verification_status")
    private VerificationStatus verificationStatus;

    @Column("user_status")
    private UserStatus userStatus;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "address", "users" }, allowSetters = true)
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
            ", userStatus='" + getUserStatus() + "'" +
            "}";
    }
}
