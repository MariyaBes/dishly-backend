package com.virality.dishly.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.virality.dishly.domain.enumeration.ChiefStatus;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Chief.
 */
@Table("chief")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Chief implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("rating")
    private Float rating;

    @Column("chief_status")
    private ChiefStatus chiefStatus;

    @Column("about")
    private String about;

    @Column("additional_links")
    private String additionalLinks;

    @Column("education_document")
    private String educationDocument;

    @Column("medical_book")
    private String medicalBook;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "dishes", "chief" }, allowSetters = true)
    private Set<Menu> menus = new HashSet<>();

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "chief" }, allowSetters = true)
    private Set<ChiefLinks> chiefLinks = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Chief id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Float getRating() {
        return this.rating;
    }

    public Chief rating(Float rating) {
        this.setRating(rating);
        return this;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }

    public ChiefStatus getChiefStatus() {
        return this.chiefStatus;
    }

    public Chief chiefStatus(ChiefStatus chiefStatus) {
        this.setChiefStatus(chiefStatus);
        return this;
    }

    public void setChiefStatus(ChiefStatus chiefStatus) {
        this.chiefStatus = chiefStatus;
    }

    public String getAbout() {
        return this.about;
    }

    public Chief about(String about) {
        this.setAbout(about);
        return this;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getAdditionalLinks() {
        return this.additionalLinks;
    }

    public Chief additionalLinks(String additionalLinks) {
        this.setAdditionalLinks(additionalLinks);
        return this;
    }

    public void setAdditionalLinks(String additionalLinks) {
        this.additionalLinks = additionalLinks;
    }

    public String getEducationDocument() {
        return this.educationDocument;
    }

    public Chief educationDocument(String educationDocument) {
        this.setEducationDocument(educationDocument);
        return this;
    }

    public void setEducationDocument(String educationDocument) {
        this.educationDocument = educationDocument;
    }

    public String getMedicalBook() {
        return this.medicalBook;
    }

    public Chief medicalBook(String medicalBook) {
        this.setMedicalBook(medicalBook);
        return this;
    }

    public void setMedicalBook(String medicalBook) {
        this.medicalBook = medicalBook;
    }

    public Set<Menu> getMenus() {
        return this.menus;
    }

    public void setMenus(Set<Menu> menus) {
        if (this.menus != null) {
            this.menus.forEach(i -> i.setChief(null));
        }
        if (menus != null) {
            menus.forEach(i -> i.setChief(this));
        }
        this.menus = menus;
    }

    public Chief menus(Set<Menu> menus) {
        this.setMenus(menus);
        return this;
    }

    public Chief addMenu(Menu menu) {
        this.menus.add(menu);
        menu.setChief(this);
        return this;
    }

    public Chief removeMenu(Menu menu) {
        this.menus.remove(menu);
        menu.setChief(null);
        return this;
    }

    public Set<ChiefLinks> getChiefLinks() {
        return this.chiefLinks;
    }

    public void setChiefLinks(Set<ChiefLinks> chiefLinks) {
        if (this.chiefLinks != null) {
            this.chiefLinks.forEach(i -> i.setChief(null));
        }
        if (chiefLinks != null) {
            chiefLinks.forEach(i -> i.setChief(this));
        }
        this.chiefLinks = chiefLinks;
    }

    public Chief chiefLinks(Set<ChiefLinks> chiefLinks) {
        this.setChiefLinks(chiefLinks);
        return this;
    }

    public Chief addChiefLinks(ChiefLinks chiefLinks) {
        this.chiefLinks.add(chiefLinks);
        chiefLinks.setChief(this);
        return this;
    }

    public Chief removeChiefLinks(ChiefLinks chiefLinks) {
        this.chiefLinks.remove(chiefLinks);
        chiefLinks.setChief(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Chief)) {
            return false;
        }
        return getId() != null && getId().equals(((Chief) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Chief{" +
            "id=" + getId() +
            ", rating=" + getRating() +
            ", chiefStatus='" + getChiefStatus() + "'" +
            ", about='" + getAbout() + "'" +
            ", additionalLinks='" + getAdditionalLinks() + "'" +
            ", educationDocument='" + getEducationDocument() + "'" +
            ", medicalBook='" + getMedicalBook() + "'" +
            "}";
    }
}
