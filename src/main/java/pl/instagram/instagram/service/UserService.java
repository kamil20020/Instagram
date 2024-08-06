package pl.instagram.instagram.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.instagram.instagram.exception.ConflictException;
import pl.instagram.instagram.exception.EntityNotFoundException;
import pl.instagram.instagram.model.entity.UserEntity;
import pl.instagram.instagram.repository.UserRepository;
import pl.instagram.instagram.specification.UserSpecification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.data.jpa.domain.Specification.where;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Override
    public UserEntity getUserById(UUID id) throws EntityNotFoundException {
        return userRepository.findById(id).orElseThrow(
            () -> new EntityNotFoundException("Nie istnieje użytkownik o takim id")
        );
    }

    @Override
    public UUID getUserIdByUserAccountId(String userAccountId) throws EntityNotFoundException {
        return getUserByUserAccountId(userAccountId).getId();
    }

    @Override
    public UserEntity getUserByUserAccountId(String userAccountId) throws EntityNotFoundException {
        return userRepository.findByUserAccountId(userAccountId).orElseThrow(
            () -> new EntityNotFoundException("Nie istnieje użytkownik o takim id konta")
        );
    }

    @Override
    public List<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public List<UserEntity> getUsersByIds(List<UUID> ids) throws IllegalArgumentException, EntityNotFoundException{

        if(ids == null){
            throw new IllegalArgumentException("Nie podano id użytkowników");
        }

        return ids.stream()
            .map(this::getUserById)
            .collect(Collectors.toList());
    }

    @Override
    public List<UserEntity> searchUsers(String phrase) {

        if(phrase.matches(".*\\s.+")){

            String[] splitInput = phrase.split("\\s");

            return userRepository.findAll(
                where(
                    (UserSpecification.userAboutFirstname(splitInput[0]).and(
                        UserSpecification.userAboutSurname(splitInput[1])
                    )).or(
                        UserSpecification.userAboutSurname(splitInput[0]).and(
                            UserSpecification.userAboutFirstname(splitInput[1])
                        )
                    )
                )
            );
        }

        return userRepository.findAll(
            where(
                UserSpecification.userAboutNickname(phrase).or(
                    UserSpecification.userAboutFirstname(phrase).or(
                        UserSpecification.userAboutSurname(phrase)
                    )
                )
            )
        );
    }

    @Override
    public UUID createUser(String userAccountId) throws ConflictException {

        if(userRepository.existsByUserAccountId(userAccountId)){
            throw new ConflictException("Istnieje już użytkownik o takim id konta");
        }

        UserEntity newUserEntity = UserEntity.builder()
            .userAccountId(userAccountId)
            .creationDatetime(LocalDateTime.now())
            .followers(0)
            .followings(0)
            .numberOfPosts(0)
            .build();

        return userRepository.save(newUserEntity).getId();
    }


    @Override
    @Transactional
    public UserEntity patchUser(String userAccountId, UserEntity updateUser) throws EntityNotFoundException {

        UserEntity loggedUser = getUserByUserAccountId(userAccountId);

        if(updateUser.getNickname() != null){
            loggedUser.setNickname(updateUser.getNickname());
        }

        if(updateUser.getFirstname() != null){
            loggedUser.setFirstname(updateUser.getFirstname());
        }

        if(updateUser.getSurname() != null){
            loggedUser.setSurname(updateUser.getSurname());
        }

        if(updateUser.getAvatar() != null){
            loggedUser.setAvatar(updateUser.getAvatar());
        }

        return loggedUser;
    }
}
