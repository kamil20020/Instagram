package pl.instagram.instagram.mapper;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class UUIDMapperTest {

    private final UUIDMapper uuidMapper = new UUIDMapperImpl();

    @Test
    void shouldConvertStrToUUIDWhenInputIsValidString() {

        String uuidStr = "7db28515-bd44-4092-b7bf-98b9a4e7a8ef";
        UUID result = uuidMapper.strToUUID(uuidStr, "");

        assertEquals(result.toString(), uuidStr);
    }

    @Test
    void shouldConvertStrToUUIDWhenInputIsNull() {

        UUID result = uuidMapper.strToUUID(null, "");

        assertNull(result);
    }

    @Test
    void shouldNotConvertStrToUUIDWhenInputIsInvalidString() {

        String uuidStr = "7db28515-bd44-b7bf-98b9a4e7a8ef";

        assertThrows(
            IllegalArgumentException.class,
            () -> uuidMapper.strToUUID(uuidStr, "")
        );
    }

    @Test
    void shouldConvertStrListToUUIDListWhenIsValidStrsList() {

        List<String> idsStrs = List.of(
            "7db28515-bd44-4092-b7bf-98b9a4e7a8ef",
            "a2bcca9a-c952-43cf-b9b3-a254c1f93ffe"
        );

        List<UUID> ids = uuidMapper.strListToUUIDList(idsStrs, "");

        for(int i=0; i < ids.size(); i++){

            assertEquals(ids.get(0).toString(), idsStrs.get(0));
        }
    }

    @Test
    void shouldConvertStrListToUUIDListWhenStrsListIsNull() {

        assertThat(uuidMapper.strListToUUIDList(null, null)).isNull();
    }

    @Test
    void shouldConvertUuidToStrWhenUuidIsNotNull() {

        UUID id = UUID.randomUUID();

        String result = uuidMapper.uuidToStr(id);

        assertThat(result).isNotNull();
    }

    @Test
    void shouldConvertUuidToStrWhenUuidIsNull() {

        assertThat(uuidMapper.uuidToStr(null)).isNull();
    }
}