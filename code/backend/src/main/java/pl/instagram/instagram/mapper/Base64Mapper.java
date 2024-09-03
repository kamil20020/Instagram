package pl.instagram.instagram.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.util.Base64;

@Mapper(componentModel = "spring")
public interface Base64Mapper {

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

		var decoder = Base64.getMimeDecoder();
        return decoder.decode(base64);
    }
}
