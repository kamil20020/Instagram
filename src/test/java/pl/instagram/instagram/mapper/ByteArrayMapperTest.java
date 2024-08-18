package pl.instagram.instagram.mapper;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ByteArrayMapperTest {

    private final ByteArrayMapper byteArrayMapper = new ByteArrayMapperImpl();

    @Test
    void shouldConvertByteArrayToBase64WhenInputIsNotNull() {

        byte[] input = ("kamil").getBytes(StandardCharsets.UTF_8);

        String output = byteArrayMapper.byteArrayToBase64(input);
    }

    @Test
    void shouldConvertByteArrayToBase64WhenInputIsNull() {

        assertThat(byteArrayMapper.byteArrayToBase64(null)).isNull();
    }

    @Test
    void shouldConvertBase64ToByteArrayWhenInputIsNotNull() {

        String input = "a2FtaWw=";

        byte[] output = byteArrayMapper.base64ToByteArray(input);
    }

    @Test
    void shouldConvertBase64ToByteArrayWhenInputIsNull() {

        assertNull(byteArrayMapper.base64ToByteArray(null));
    }
}