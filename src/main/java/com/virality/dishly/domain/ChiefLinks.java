package com.virality.dishly.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A ChiefLinks.
 */
@Table("chief_links")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ChiefLinks implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("telegram_link")
    private String telegramLink;

    @Column("vk_link")
    private String vkLink;

    @Column("odnoklassniki_link")
    private String odnoklassnikiLink;

    @Column("youtube_link")
    private String youtubeLink;

    @Column("rutube_link")
    private String rutubeLink;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "menus", "chiefLinks" }, allowSetters = true)
    private Chief chief;

    @Column("chief_id")
    private Long chiefId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ChiefLinks id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTelegramLink() {
        return this.telegramLink;
    }

    public ChiefLinks telegramLink(String telegramLink) {
        this.setTelegramLink(telegramLink);
        return this;
    }

    public void setTelegramLink(String telegramLink) {
        this.telegramLink = telegramLink;
    }

    public String getVkLink() {
        return this.vkLink;
    }

    public ChiefLinks vkLink(String vkLink) {
        this.setVkLink(vkLink);
        return this;
    }

    public void setVkLink(String vkLink) {
        this.vkLink = vkLink;
    }

    public String getOdnoklassnikiLink() {
        return this.odnoklassnikiLink;
    }

    public ChiefLinks odnoklassnikiLink(String odnoklassnikiLink) {
        this.setOdnoklassnikiLink(odnoklassnikiLink);
        return this;
    }

    public void setOdnoklassnikiLink(String odnoklassnikiLink) {
        this.odnoklassnikiLink = odnoklassnikiLink;
    }

    public String getYoutubeLink() {
        return this.youtubeLink;
    }

    public ChiefLinks youtubeLink(String youtubeLink) {
        this.setYoutubeLink(youtubeLink);
        return this;
    }

    public void setYoutubeLink(String youtubeLink) {
        this.youtubeLink = youtubeLink;
    }

    public String getRutubeLink() {
        return this.rutubeLink;
    }

    public ChiefLinks rutubeLink(String rutubeLink) {
        this.setRutubeLink(rutubeLink);
        return this;
    }

    public void setRutubeLink(String rutubeLink) {
        this.rutubeLink = rutubeLink;
    }

    public Chief getChief() {
        return this.chief;
    }

    public void setChief(Chief chief) {
        this.chief = chief;
        this.chiefId = chief != null ? chief.getId() : null;
    }

    public ChiefLinks chief(Chief chief) {
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
        if (!(o instanceof ChiefLinks)) {
            return false;
        }
        return getId() != null && getId().equals(((ChiefLinks) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ChiefLinks{" +
            "id=" + getId() +
            ", telegramLink='" + getTelegramLink() + "'" +
            ", vkLink='" + getVkLink() + "'" +
            ", odnoklassnikiLink='" + getOdnoklassnikiLink() + "'" +
            ", youtubeLink='" + getYoutubeLink() + "'" +
            ", rutubeLink='" + getRutubeLink() + "'" +
            "}";
    }
}
