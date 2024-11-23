package com.virality.dishly.domain;

import static com.virality.dishly.domain.ChiefTestSamples.*;
import static com.virality.dishly.domain.SignatureDishTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.virality.dishly.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SignatureDishTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SignatureDish.class);
        SignatureDish signatureDish1 = getSignatureDishSample1();
        SignatureDish signatureDish2 = new SignatureDish();
        assertThat(signatureDish1).isNotEqualTo(signatureDish2);

        signatureDish2.setId(signatureDish1.getId());
        assertThat(signatureDish1).isEqualTo(signatureDish2);

        signatureDish2 = getSignatureDishSample2();
        assertThat(signatureDish1).isNotEqualTo(signatureDish2);
    }

    @Test
    void chiefTest() {
        SignatureDish signatureDish = getSignatureDishRandomSampleGenerator();
        Chief chiefBack = getChiefRandomSampleGenerator();

        signatureDish.setChief(chiefBack);
        assertThat(signatureDish.getChief()).isEqualTo(chiefBack);

        signatureDish.chief(null);
        assertThat(signatureDish.getChief()).isNull();
    }
}
