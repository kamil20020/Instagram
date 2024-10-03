package pl.instagram.chat.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import pl.instagram.chat.exception.UserIsNotLoggedException;

import java.security.Principal;
import java.util.UUID;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

@Service
@Slf4j
public class AuthService {

    public boolean isUserLogged(){

        SecurityContext context = SecurityContextHolder.getContext();

        if(context.getAuthentication() == null){
            return false;
        }

        Authentication authentication = context.getAuthentication();

        return authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken);
    }

    public String getLoggedUserAccountId() throws UserIsNotLoggedException, IllegalArgumentException {

        if(!isUserLogged()){
            throw new UserIsNotLoggedException();
        }

        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
