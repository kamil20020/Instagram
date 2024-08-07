package pl.instagram.instagram.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.instagram.instagram.exception.ConflictException;
import pl.instagram.instagram.exception.EntityNotFoundException;
import pl.instagram.instagram.model.entity.UserEntity;
import pl.instagram.instagram.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.springframework.data.jpa.domain.Specification.*;
import static pl.instagram.instagram.specification.UserSpecification.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public boolean existsById(UUID id){
        return userRepository.existsById(id);
    }

    public UserEntity getUserById(UUID id) throws EntityNotFoundException {
        return userRepository.findById(id)
            .orElseThrow(
                () -> new EntityNotFoundException("Nie istnieje użytkownik o takim id")
            );
    }

    public UUID getUserIdByUserAccountId(String accountId) throws EntityNotFoundException {
        return getUserByUserAccountId(accountId).getId();
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

            String[] splitInput = phrase.split("\\s");

            return userRepository.findAll(
                where(
                    anyOf(
                        allOf(
                            userAboutFirstname(splitInput[0]),
                            userAboutSurname(splitInput[1])
                        ),
                        allOf(
                            userAboutSurname(splitInput[0]),
                            userAboutFirstname(splitInput[1])
                        )
                    )
                ),
                pageable
            );
        }

        return userRepository.findAll(
            where(
                anyOf(
                    userAboutNickname(phrase),
                    userAboutFirstname(phrase),
                    userAboutSurname(phrase)
                )
            ),
            pageable
        );
    }

    public UUID createUser(String accountId) throws ConflictException {

        if(userRepository.existsByAccountId(accountId)){
            throw new ConflictException("Istnieje już użytkownik o takim id konta");
        }

        UserEntity newUser = UserEntity.builder()
            .accountId(accountId)
            .creationDatetime(LocalDateTime.now())
            .build();

        return userRepository.save(newUser).getId();
    }


    @Transactional
    public UserEntity patchUser(String accountId, UserEntity updateData) throws EntityNotFoundException {

        UserEntity loggedUser = getUserByUserAccountId(accountId);

        if(updateData.getNickname() != null){
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
}
