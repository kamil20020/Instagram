package pl.instagram.instagram.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.instagram.instagram.model.api.response.CommentData;
import pl.instagram.instagram.model.entity.CommentEntity;

@Mapper(uses = {DateTimeMapper.class, UserMapper.class}, componentModel = "spring")
public interface CommentMapper {

    @Mapping(source = "creationDatetime", target = "creationDatetime", qualifiedByName = "localDatetimeToOffsetDatetime")
    CommentData commentEntityToCommentData(CommentEntity commentEntity);
}
