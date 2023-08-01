package pl.instagram.instagram.service;

import pl.instagram.instagram.model.entity.UserEntity;

import java.util.List;
import java.util.UUID;

public interface UserService {

    UserEntity getUserById(UUID id);
    List<UserEntity> getAllUsers();
    List<UserEntity> getUsersByIds(List<UUID> ids);
    List<UserEntity> searchUsers(String phrase);
    void createUser(UUID userAccountId);
}
