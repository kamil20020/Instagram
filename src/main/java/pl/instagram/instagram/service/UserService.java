package pl.instagram.instagram.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.instagram.instagram.exception.ConflictException;
import pl.instagram.instagram.exception.EntityNotFoundException;
import pl.instagram.instagram.exception.NonLoggedException;
import pl.instagram.instagram.mapper.UserMapper;
import pl.instagram.instagram.model.domain.UserEntityForLoggedUser;
import pl.instagram.instagram.model.entity.UserEntity;
import pl.instagram.instagram.repository.FollowerRepository;
import pl.instagram.instagram.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final FollowerRepository followerRepository;

    private final AuthService authService;

    private final UserMapper userMapper;

    public boolean existsById(UUID id){
        return userRepository.existsById(id);
    }

    public UserEntity getLoggedUser() throws NonLoggedException, EntityNotFoundException {

        String loggedUserAccountId = authService.getLoggedUserAccountId();

        return getUserByUserAccountId(loggedUserAccountId);
    }

    public UserEntity getUserById(UUID id) throws EntityNotFoundException {
        return userRepository.findById(id)
            .orElseThrow(
                () -> new EntityNotFoundException("Nie istnieje użytkownik o takim id")
            );
    }

    public UserEntityForLoggedUser getUserByIdForLoggedUser(UUID id) throws EntityNotFoundException {

        UserEntity foundUser = getUserById(id);

        boolean didLoggedUserFollow = false;

        if(authService.isUserLogged()){

            String loggedUserAccountId = authService.getLoggedUserAccountId();
            didLoggedUserFollow = followerRepository.existsByFollowerAccountIdAndFollowedId(loggedUserAccountId, id);
        }

        UserEntityForLoggedUser convertedUser = userMapper.userEntityToUserEntityForLoggedUser(foundUser);
        convertedUser.setDidLoggedUserFollow(didLoggedUserFollow);

        return convertedUser;
    }

    public UserEntity getUserByUserAccountId(String accountId) throws EntityNotFoundException {
        return userRepository.findByAccountId(accountId)
            .orElseThrow(
                () -> new EntityNotFoundException("Nie istnieje użytkownik o takim id konta")
            );
    }

    public List<UserEntity> getUsersByIds(List<UUID> ids) throws IllegalArgumentException {

        if(ids == null || ids.size() == 0){
            throw new IllegalArgumentException("Nie podano id użytkowników");
        }

        return userRepository.findAllById(ids);
    }

    public Page<UserEntity> searchUsers(String phrase, Pageable pageable) throws IllegalArgumentException {

        if(pageable == null){
            throw new IllegalArgumentException("Paginacja jest wymagana");
        }

        if(phrase.matches(".+\\s.+")){

            return userRepository.searchByFirstnameAndSurname(phrase, pageable);
        }

        return userRepository.searchByFirstnameOrSurnameOrNickname(phrase, pageable);
    }

    public UUID createUser(String accountId) throws ConflictException {

        if(userRepository.existsByAccountId(accountId)){
            throw new ConflictException("Istnieje już użytkownik o takim id konta");
        }

        UserEntity newUser = UserEntity.builder()
            .accountId(accountId)
            .creationDatetime(LocalDateTime.now())
            .followers(0)
            .followings(0)
            .numberOfPosts(0)
            .posts(new HashSet<>())
            .comments(new HashSet<>())
            .followersUsers(new HashSet<>())
            .likedComments(new HashSet<>())
            .build();

        return userRepository.save(newUser).getId();
    }

    @Transactional
    public UserEntity patchUser(UserEntity updateData) throws NonLoggedException, EntityNotFoundException, ConflictException {

        UserEntity loggedUser = getLoggedUser();

        if(updateData.getNickname() != null){

            if(userRepository.existsByNicknameContainingIgnoreCase(updateData.getNickname())){
                throw new ConflictException("Istnieje już użytkownik o takim pseudonimie");
            }

            loggedUser.setNickname(updateData.getNickname());
        }

        if(updateData.getFirstname() != null){
            loggedUser.setFirstname(updateData.getFirstname());
        }

        if(updateData.getSurname() != null){
            loggedUser.setSurname(updateData.getSurname());
        }

        if(updateData.getAvatar() != null){
            loggedUser.setAvatar(updateData.getAvatar());
        }

        return loggedUser;
    }

    @Transactional
    public UserEntity fillPersonalData(UserEntity userPersonalData) throws NonLoggedException, EntityNotFoundException, ConflictException {

        UserEntity loggedUser = getLoggedUser();

        if(userRepository.existsByNicknameContainingIgnoreCase(userPersonalData.getNickname())){
            throw new ConflictException("Istnieje już użytkownik o takim pseudonimie");
        }

        loggedUser.setFirstname(userPersonalData.getFirstname());
        loggedUser.setSurname(userPersonalData.getSurname());
        loggedUser.setNickname(userPersonalData.getNickname());

        return loggedUser;
    }
}
