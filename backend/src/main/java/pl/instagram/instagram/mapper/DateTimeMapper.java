package pl.instagram.instagram.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Mapper(componentModel = "spring")
public interface DateTimeMapper {

    DateTimeMapper INSTANCE = Mappers.getMapper(DateTimeMapper.class);

    @Named("localDatetimeToOffsetDatetime")
    default OffsetDateTime map(LocalDateTime localDateTime){
        return localDateTime.atOffset(ZoneOffset.UTC);
    }
}
