package com.virality.dishly.domain;

import static com.virality.dishly.domain.AddressTestSamples.*;
import static com.virality.dishly.domain.CityTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.virality.dishly.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AddressTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Address.class);
        Address address1 = getAddressSample1();
        Address address2 = new Address();
        assertThat(address1).isNotEqualTo(address2);

        address2.setId(address1.getId());
        assertThat(address1).isEqualTo(address2);

        address2 = getAddressSample2();
        assertThat(address1).isNotEqualTo(address2);
    }

    @Test
    void cityTest() {
        Address address = getAddressRandomSampleGenerator();
        City cityBack = getCityRandomSampleGenerator();

        address.setCity(cityBack);
        assertThat(address.getCity()).isEqualTo(cityBack);

        address.city(null);
        assertThat(address.getCity()).isNull();
    }
}
