package pl.instagram.instagram.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface UUIDMapper {

    default UUID strToUUID(String idStr, String messageEnding) throws IllegalArgumentException{

        try{
            return UUID.fromString(idStr);
        }
        catch(IllegalArgumentException e){
            throw new IllegalArgumentException("Podano nieprawidłowe id " + messageEnding);
        }
    }

    ArrayList<UUID> strListToUUIDList(List<String> idsList, String messageEnding);

    default String uuidToStr(UUID id){
        return id.toString();
    }
}
