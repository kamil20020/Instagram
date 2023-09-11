package pl.instagram.instagram.service;

import pl.instagram.instagram.model.api.request.UpdateUser;
import pl.instagram.instagram.model.entity.UserEntity;

import java.util.List;
import java.util.UUID;

public interface UserService {

    UserEntity getUserById(UUID id);
    UUID getUserIdByUserAccountId(String userAccountId);
    UserEntity getUserByUserAccountId(String userAccountId);
    List<UserEntity> getAllUsers();
    List<UserEntity> getUsersByIds(List<UUID> ids);
    List<UserEntity> searchUsers(String phrase);
    UUID createUser(String userAccountId);
    UserEntity patchUser(String userAccountId, UserEntity updateUser);
}
