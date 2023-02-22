package pl.instagram.instagram.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.instagram.instagram.exception.EntityNotFoundException;
import pl.instagram.instagram.model.entity.UserEntity;
import pl.instagram.instagram.repository.UserRepository;
import pl.instagram.instagram.service.UserService;
import pl.instagram.instagram.specification.UserSpecification;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.data.jpa.domain.Specification.where;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserEntity getUserById(Integer id) throws EntityNotFoundException {

        Optional<UserEntity> foundUserEntityOpt = userRepository.findById(id);

        if(foundUserEntityOpt.isEmpty()){
            throw new EntityNotFoundException("Nie istnieje użytkownik o takim id");
        }

        return foundUserEntityOpt.get();
    }

    @Override
    public List<UserEntity> getAllUsers() {

        return userRepository.findAll();
    }

    @Override
    public List<UserEntity> getUsersByIds(List<Integer> ids) throws IllegalArgumentException, EntityNotFoundException{

        if(ids == null){
            throw new IllegalArgumentException("Nie podano id użytkowników");
        }

        return ids.stream()
            .map(this::getUserById)
            .collect(Collectors.toList());
    }

    @Override
    public List<UserEntity> searchUsers(String input) {

        if(input.matches(".*\\s.+")){

            String[] splitInput = input.split("\\s");

            return userRepository.findAll(
                where(
                    UserSpecification.userAboutFirstname(splitInput[0]).and(
                        UserSpecification.userAboutSurname(splitInput[1])
                    )
                )
            );
        }

        return userRepository.findAll(
            where(
                UserSpecification.userAboutNickname(input).or(
                    UserSpecification.userAboutFirstname(input).or(
                        UserSpecification.userAboutSurname(input)
                    )
                )
            )
        );
    }
}
