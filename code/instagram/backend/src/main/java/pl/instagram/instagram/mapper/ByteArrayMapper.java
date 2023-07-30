package pl.instagram.instagram.mapper;

import org.apache.commons.lang3.ArrayUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.Base64;

@Mapper
public interface ByteArrayMapper {

    ByteArrayMapper INSTANCE = Mappers.getMapper(ByteArrayMapper.class);

    @Named("byteArrayToBase64")
    default String byteArrayToBase64(byte[] byteArr) {
        if(byteArr == null){
            return null;
        }

        return Base64.getEncoder().encodeToString(byteArr);
    }

    @Named("base64ToByteArray")
    default byte[] base64ToByteArray(String base64) {
        if(base64 == null){
            return null;
        }

        return Base64.getDecoder().decode(base64);
    }
}
