package pl.instagram.instagram.mapper;

import org.mapstruct.Mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UUIDMapper {

    default UUID strToUUID(String idStr, String messageEnding) throws IllegalArgumentException{

        if(idStr == null) {
            return null;
        }

        try{
            return UUID.fromString(idStr);
        }
        catch(IllegalArgumentException e){
            throw new IllegalArgumentException("Podano nieprawidłowe id " + messageEnding);
        }
    }

    default List<UUID> strListToUUIDList(List<String> idsList, String messageEnding) throws IllegalArgumentException{

        if(idsList == null){
            return null;
        }

        return idsList.stream()
            .map(idStr -> strToUUID(idStr, messageEnding))
            .collect(Collectors.toList());
    }

    default String uuidToStr(UUID id){

        if(id == null){
            return null;
        }

        return id.toString();
    }
}
