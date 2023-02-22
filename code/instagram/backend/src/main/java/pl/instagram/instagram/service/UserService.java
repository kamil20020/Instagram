package pl.instagram.instagram.service;

import pl.instagram.instagram.model.entity.UserEntity;

import java.util.List;

public interface UserService {

    UserEntity getUserById(Integer id);
    List<UserEntity> getAllUsers();
    List<UserEntity> getUsersByIds(List<Integer> ids);
    List<UserEntity> searchUsers(String input);
}
