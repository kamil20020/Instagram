package pl.instagram.instagram.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.instagram.instagram.exception.ConflictException;
import pl.instagram.instagram.exception.EntityNotFoundException;
import pl.instagram.instagram.mapper.UserMapper;
import pl.instagram.instagram.model.domain.Followers;
import pl.instagram.instagram.model.domain.UserEntityForLoggedUser;
import pl.instagram.instagram.model.entity.FollowerEntity;
import pl.instagram.instagram.model.entity.UserEntity;
import pl.instagram.instagram.repository.FollowerRepository;
import pl.instagram.instagram.repository.UserRepository;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FollowerService {

    private final UserRepository userRepository;
    private final FollowerRepository followerRepository;

    private final UserService userService;
    private final AuthService authService;

    public Page<UserEntity> getFollowersPage(UUID followedId, Pageable pageable) throws IllegalArgumentException, EntityNotFoundException {

        if(pageable == null){
            throw new IllegalArgumentException("Paginacja jest wymagana");
        }

        if(!userRepository.existsById(followedId)){
            throw new EntityNotFoundException("Nie istnieje użytkownik o takim id");
        }

        return userRepository.findByFollowersUsersFollowedId(followedId, pageable);
    }

    public Page<UserEntity> getFollowedPage(UUID followerId, Pageable pageable) throws IllegalArgumentException {

        if(pageable == null){
            throw new IllegalArgumentException("Paginacja jest wymagana");
        }

        return userRepository.findByFollowedUsersFollowerId(followerId, pageable);
    }

    @Transactional
    public UserEntity createFollow(UUID followedId) throws ConflictException, EntityNotFoundException {

        String loggedUserAccountId = authService.getLoggedUserAccountId();

        if(followerRepository.existsByFollowerAccountIdAndFollowedId(loggedUserAccountId, followedId)){
            throw new ConflictException("Użytkownik już zaobserwował podanego innego użytkownika");
        }

        UserEntity loggedUser = userService.getLoggedUser();
        UserEntity followedUser = userService.getUserById(followedId);

        FollowerEntity followerEntity = new FollowerEntity(followedUser, loggedUser);
        followerEntity = followerRepository.save(followerEntity);

        loggedUser.getFollowedUsers().add(followerEntity);
        loggedUser.setFollowings(loggedUser.getFollowings() + 1);

        followedUser.getFollowersUsers().add(followerEntity);
        followedUser.setFollowers(followedUser.getFollowers() + 1);

        return loggedUser;
    }

    @Transactional
    public void deleteFollow(UUID followedId) throws EntityNotFoundException {

        String loggedUserAccountId = authService.getLoggedUserAccountId();

        FollowerEntity foundFollowerEntity = followerRepository.findByFollowerAccountIdAndFollowedId(loggedUserAccountId, followedId)
            .orElseThrow(() -> new EntityNotFoundException("Użytkownik nie zaobserwował podanego innego użytkownika"));

        UserEntity loggedUser = foundFollowerEntity.getFollower();
        UserEntity followedUser = foundFollowerEntity.getFollowed();

        loggedUser.getFollowedUsers().remove(foundFollowerEntity);
        loggedUser.setFollowings(loggedUser.getFollowings() - 1);

        followedUser.getFollowersUsers().remove(foundFollowerEntity);
        followedUser.setFollowers(followedUser.getFollowers() - 1);

        followerRepository.delete(foundFollowerEntity);
    }
}
