package com.virality.dishly.domain;

import static com.virality.dishly.domain.ChiefLinksTestSamples.*;
import static com.virality.dishly.domain.ChiefTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.virality.dishly.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ChiefLinksTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ChiefLinks.class);
        ChiefLinks chiefLinks1 = getChiefLinksSample1();
        ChiefLinks chiefLinks2 = new ChiefLinks();
        assertThat(chiefLinks1).isNotEqualTo(chiefLinks2);

        chiefLinks2.setId(chiefLinks1.getId());
        assertThat(chiefLinks1).isEqualTo(chiefLinks2);

        chiefLinks2 = getChiefLinksSample2();
        assertThat(chiefLinks1).isNotEqualTo(chiefLinks2);
    }

    @Test
    void chiefTest() {
        ChiefLinks chiefLinks = getChiefLinksRandomSampleGenerator();
        Chief chiefBack = getChiefRandomSampleGenerator();

        chiefLinks.setChief(chiefBack);
        assertThat(chiefLinks.getChief()).isEqualTo(chiefBack);

        chiefLinks.chief(null);
        assertThat(chiefLinks.getChief()).isNull();
    }
}
