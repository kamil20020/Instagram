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
    default String byteArrayToBase64(Byte[] byteArr) {
        if(byteArr == null){
            return null;
        }
        byte[] primitiveByteArr = ArrayUtils.toPrimitive(byteArr);
        return Base64.getMimeEncoder().encodeToString(primitiveByteArr);
    }

    @Named("base64ToByteArray")
    default Byte[] base64ToByteArray(String base64) {
        if(base64 == null){
            return null;
        }
        byte[] byteArr = Base64.getMimeDecoder().decode(base64);
        return ArrayUtils.toObject(byteArr);
    }
}
