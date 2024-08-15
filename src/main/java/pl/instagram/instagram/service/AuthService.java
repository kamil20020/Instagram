package pl.instagram.instagram.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import pl.instagram.instagram.exception.NonLoggedException;
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

    public void checkLoggedUserResourceAuthorship(Predicate<String> isAuthorPredicate) throws NonLoggedException, UserIsNotResourceAuthorException{

        String loggedUserAccountId = getLoggedUserAccountId();

        if(!isAuthorPredicate.test(loggedUserAccountId)){
            throw new UserIsNotResourceAuthorException("Użytkownik nie jest autorem podanego zasobu");
        }
    }

    public Authentication getLoggedUserDetails() throws NonLoggedException{

        Authentication authDetails = SecurityContextHolder.getContext().getAuthentication();

        if(authDetails == null){
            throw new NonLoggedException();
        }

        return authDetails;
    }

    public String getLoggedUserAccountId() throws NonLoggedException {

        return ((Principal) getLoggedUserDetails().getPrincipal()).getName();
    }
}
