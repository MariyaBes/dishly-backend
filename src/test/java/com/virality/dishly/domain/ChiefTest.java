package com.virality.dishly.domain;

import static com.virality.dishly.domain.ChiefLinksTestSamples.*;
import static com.virality.dishly.domain.ChiefTestSamples.*;
import static com.virality.dishly.domain.MenuTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.virality.dishly.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ChiefTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Chief.class);
        Chief chief1 = getChiefSample1();
        Chief chief2 = new Chief();
        assertThat(chief1).isNotEqualTo(chief2);

        chief2.setId(chief1.getId());
        assertThat(chief1).isEqualTo(chief2);

        chief2 = getChiefSample2();
        assertThat(chief1).isNotEqualTo(chief2);
    }

    @Test
    void menuTest() {
        Chief chief = getChiefRandomSampleGenerator();
        Menu menuBack = getMenuRandomSampleGenerator();

        chief.addMenu(menuBack);
        assertThat(chief.getMenus()).containsOnly(menuBack);
        assertThat(menuBack.getChief()).isEqualTo(chief);

        chief.removeMenu(menuBack);
        assertThat(chief.getMenus()).doesNotContain(menuBack);
        assertThat(menuBack.getChief()).isNull();

        chief.menus(new HashSet<>(Set.of(menuBack)));
        assertThat(chief.getMenus()).containsOnly(menuBack);
        assertThat(menuBack.getChief()).isEqualTo(chief);

        chief.setMenus(new HashSet<>());
        assertThat(chief.getMenus()).doesNotContain(menuBack);
        assertThat(menuBack.getChief()).isNull();
    }

    @Test
    void chiefLinksTest() {
        Chief chief = getChiefRandomSampleGenerator();
        ChiefLinks chiefLinksBack = getChiefLinksRandomSampleGenerator();

        chief.addChiefLinks(chiefLinksBack);
        assertThat(chief.getChiefLinks()).containsOnly(chiefLinksBack);
        assertThat(chiefLinksBack.getChief()).isEqualTo(chief);

        chief.removeChiefLinks(chiefLinksBack);
        assertThat(chief.getChiefLinks()).doesNotContain(chiefLinksBack);
        assertThat(chiefLinksBack.getChief()).isNull();

        chief.chiefLinks(new HashSet<>(Set.of(chiefLinksBack)));
        assertThat(chief.getChiefLinks()).containsOnly(chiefLinksBack);
        assertThat(chiefLinksBack.getChief()).isEqualTo(chief);

        chief.setChiefLinks(new HashSet<>());
        assertThat(chief.getChiefLinks()).doesNotContain(chiefLinksBack);
        assertThat(chiefLinksBack.getChief()).isNull();
    }
}
