package pl.instagram.instagram.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import pl.instagram.instagram.exception.UserIsNotResourceAuthorException;

import java.security.Principal;
import java.util.Optional;
import java.util.function.Predicate;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    public boolean isUserLogged(){

        Optional<Authentication> authenticationOpt = Optional.ofNullable(getLoggedUserDetails());

        return authenticationOpt.isPresent() && authenticationOpt.get().isAuthenticated();
    }

    public void checkLoggedUserResourceAuthorship(Predicate<String> isAuthorPredicate) throws UserIsNotResourceAuthorException{

        String loggedUserAccountId = getLoggedUserAccountId();

        if(!isAuthorPredicate.test(loggedUserAccountId)){
            throw new UserIsNotResourceAuthorException("Użytkownik nie jest autorem podanego zasobu");
        }
    }

    public Authentication getLoggedUserDetails() {

        return SecurityContextHolder.getContext().getAuthentication();
    }

    public String getLoggedUserAccountId() {
        return ((Principal) getLoggedUserDetails().getPrincipal()).getName();
    }
}
