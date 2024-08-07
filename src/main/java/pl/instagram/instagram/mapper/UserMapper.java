package pl.instagram.instagram.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.instagram.instagram.model.api.request.UpdateUser;
import pl.instagram.instagram.model.api.response.UserHeader;
import pl.instagram.instagram.model.api.response.UserProfile;
import pl.instagram.instagram.model.entity.UserEntity;

import java.util.List;

@Mapper(uses = {ByteArrayMapper.class}, componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "avatar", target = "avatar", qualifiedByName = "byteArrayToBase64")
    UserHeader userEntityToBasicUserData(UserEntity userEntity);

    List<UserHeader> userEntityListToBasicUserDataList(List<UserEntity> userEntityList);

    @Mapping(source = "avatar", target = "avatar", qualifiedByName = "base64ToByteArray")
    UserEntity updateUserToUserEntity(UpdateUser updateUser);

    @Mapping(source = "avatar", target = "avatar", qualifiedByName = "byteArrayToBase64")
    @Mapping(expression = "java(userEntity.getPostEntityList().size())", target = "numberOfPosts")
    UserProfile userEntityToUserProfileInfo(UserEntity userEntity);
}
