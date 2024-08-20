package pl.instagram.instagram.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.instagram.instagram.model.api.request.CreatePost;
import pl.instagram.instagram.model.api.response.PostDetails;
import pl.instagram.instagram.model.api.response.PostHeader;
import pl.instagram.instagram.model.entity.PostEntity;

@Mapper(uses = {Base64Mapper.class, DateTimeMapper.class, UserMapper.class}, componentModel = "spring")
public interface PostMapper {

    @Mapping(source = "content", target = "content", qualifiedByName = "base64ToByteArray")
    PostEntity createPostToPostEntity(CreatePost createPost);

    @Mapping(source = "content", target = "content", qualifiedByName = "byteArrayToBase64")
    @Mapping(source = "creationDatetime", target = "creationDatetime", qualifiedByName = "localDatetimeToOffsetDatetime")
    PostDetails postEntityToPostDetails(PostEntity postEntity);

    @Mapping(source = "content", target = "content", qualifiedByName = "byteArrayToBase64")
    PostHeader postEntityToPostHeader(PostEntity postEntity);
}
