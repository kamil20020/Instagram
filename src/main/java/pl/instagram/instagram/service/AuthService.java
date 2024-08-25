package pl.instagram.instagram.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import pl.instagram.instagram.exception.NonLoggedException;
import pl.instagram.instagram.exception.UserIsNotResourceAuthorException;

import java.security.Principal;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    public boolean isUserLogged(){

        Optional<Authentication> authenticationOpt = Optional.ofNullable(getLoggedUserDetails());

        return authenticationOpt.isPresent() && authenticationOpt.get().isAuthenticated();
    }

    public void checkLoggedUserResourceAuthorship(UUID id, BiPredicate<UUID, String> isAuthorPredicate) throws NonLoggedException, UserIsNotResourceAuthorException{

        String loggedUserAccountId = getLoggedUserAccountId();

        if(!isAuthorPredicate.test(id, loggedUserAccountId)){
            throw new UserIsNotResourceAuthorException("Użytkownik nie jest autorem podanego zasobu");
        }
    }

    public void checkLoggedUserResourceAuthorship(String shouldBeAccountId) throws NonLoggedException, UserIsNotResourceAuthorException{

        String loggedUserAccountId = getLoggedUserAccountId();

        if(!loggedUserAccountId.equals(shouldBeAccountId)){
            throw new UserIsNotResourceAuthorException("Użytkownik nie jest autorem podanego zasobu");
        }
    }

    public Authentication getLoggedUserDetails() {

        return SecurityContextHolder.getContext().getAuthentication();
    }

    public String getLoggedUserAccountId() throws NonLoggedException {

        Authentication authDetails = getLoggedUserDetails();

        if(authDetails == null){
            throw new NonLoggedException();
        }

        return authDetails.getName();
    }
}
