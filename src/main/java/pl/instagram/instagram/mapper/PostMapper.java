package pl.instagram.instagram.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import pl.instagram.instagram.model.api.response.PostDetails;
import pl.instagram.instagram.model.entity.PostEntity;

@Mapper(uses = {ByteArrayMapper.class, DateTimeMapper.class, UserMapper.class}, componentModel = "spring")
public interface PostMapper {

    PostMapper INSTANCE = Mappers.getMapper(PostMapper.class);

    @Mapping(source = "img", target = "img", qualifiedByName = "byteArrayToBase64")
    @Mapping(source = "creationDatetime", target = "creationDatetime", qualifiedByName = "localDatetimeToOffsetDatetime")
    @Mapping(source = "userEntity", target = "userData")
    @Mapping(expression = "java(postEntity.getPostLikeEntities().size())", target = "numberOfLikes")
    @Mapping(expression = "java(postEntity.getCommentEntityList().size())", target = "numberOfComments")
    PostDetails postEntityToPostDetails(PostEntity postEntity);
}
