package pl.instagram.instagram.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.instagram.instagram.exception.EntityNotFoundException;
import pl.instagram.instagram.model.domain.Followers;
import pl.instagram.instagram.model.entity.UserEntity;
import pl.instagram.instagram.repository.UserRepository;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FollowerService {

    private final UserRepository userRepository;
    private final AuthService authService;

    public Followers getFollowersPage(UUID followedId, Pageable pageable) throws IllegalArgumentException, EntityNotFoundException {

        if(pageable == null){
            throw new IllegalArgumentException("Paginacja jest wymagana");
        }

        if(!userRepository.existsById(followedId)){
            throw new EntityNotFoundException("Nie istnieje użytkownik o takim id");
        }

        Page<UserEntity> gotFollowers = userRepository.findByFollowersUsersFollowedId(followedId, pageable);

        boolean didLoggedUserFollowed = false;

        if(authService.isUserLogged()){

            String loggedUserAccountId = authService.getLoggedUserAccountId();

            if(userRepository.existsByAccountIdAndFollowersUsersFollowedId(loggedUserAccountId, followedId)){
                didLoggedUserFollowed = true;
            }
        }

        return new Followers(gotFollowers, didLoggedUserFollowed);
    }
}
