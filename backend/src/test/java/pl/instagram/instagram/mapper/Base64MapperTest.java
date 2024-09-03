package pl.instagram.instagram.mapper;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class Base64MapperTest {

    private final Base64Mapper base64Mapper = new Base64MapperImpl();

    @Test
    void shouldConvertByteArrayToBase64WhenInputIsNotNull() {

        byte[] input = ("kamil").getBytes(StandardCharsets.UTF_8);

        String output = base64Mapper.byteArrayToBase64(input);
    }

    @Test
    void shouldConvertByteArrayToBase64WhenInputIsNull() {

        assertThat(base64Mapper.byteArrayToBase64(null)).isNull();
    }

    @Test
    void shouldConvertBase64ToByteArrayWhenInputIsNotNull() {

        String input = "a2FtaWw=";

        byte[] output = base64Mapper.base64ToByteArray(input);
    }

    @Test
    void shouldConvertBase64ToByteArrayWhenInputIsNull() {

        assertNull(base64Mapper.base64ToByteArray(null));
    }
}