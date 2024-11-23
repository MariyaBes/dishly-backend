package com.virality.dishly.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A SignatureDish.
 */
@Table("signature_dish")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SignatureDish implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("name")
    private String name;

    @Column("image")
    private String image;

    @Column("description")
    private String description;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "menus", "chiefLinks" }, allowSetters = true)
    private Chief chief;

    @Column("chief_id")
    private Long chiefId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public SignatureDish id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public SignatureDish name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return this.image;
    }

    public SignatureDish image(String image) {
        this.setImage(image);
        return this;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return this.description;
    }

    public SignatureDish description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Chief getChief() {
        return this.chief;
    }

    public void setChief(Chief chief) {
        this.chief = chief;
        this.chiefId = chief != null ? chief.getId() : null;
    }

    public SignatureDish chief(Chief chief) {
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
        if (!(o instanceof SignatureDish)) {
            return false;
        }
        return getId() != null && getId().equals(((SignatureDish) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SignatureDish{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", image='" + getImage() + "'" +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
