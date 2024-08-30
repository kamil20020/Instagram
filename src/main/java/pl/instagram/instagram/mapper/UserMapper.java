package pl.instagram.instagram.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.instagram.instagram.model.api.request.PersonalData;
import pl.instagram.instagram.model.api.request.UpdateUser;
import pl.instagram.instagram.model.api.response.UserHeader;
import pl.instagram.instagram.model.api.response.UserProfile;
import pl.instagram.instagram.model.domain.UserEntityForLoggedUser;
import pl.instagram.instagram.model.entity.UserEntity;

import java.util.List;

@Mapper(uses = {Base64Mapper.class}, componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "avatar", target = "avatar", qualifiedByName = "base64ToByteArray")
    UserEntity updateUserToUserEntity(UpdateUser updateUser);

    UserEntity userPersonalDataToUserEntity(PersonalData personalData);

    @Mapping(source = "private", target = "isPrivate")
    @Mapping(source = "verified", target = "isVerified")
    UserEntityForLoggedUser userEntityToUserEntityForLoggedUser(UserEntity userEntity);

    @Mapping(source = "avatar", target = "avatar", qualifiedByName = "byteArrayToBase64")
    @Mapping(source = "verified", target = "isVerified")
    UserHeader userEntityToUserHeader(UserEntity userEntity);

    List<UserHeader> userEntityListToUserHeaderList(List<UserEntity> userEntityList);

    @Mapping(source = "avatar", target = "avatar", qualifiedByName = "byteArrayToBase64")
    @Mapping(source = "private", target = "isPrivate")
    @Mapping(source = "verified", target = "isVerified")
    UserProfile userEntityForLoggedToUserProfile(UserEntityForLoggedUser userEntityForLoggedUser);

    @Mapping(source = "avatar", target = "avatar", qualifiedByName = "byteArrayToBase64")
    @Mapping(source = "private", target = "isPrivate")
    @Mapping(source = "verified", target = "isVerified")
    UserProfile userEntityToUserProfileInfo(UserEntity userEntity);
}
