package pl.instagram.instagram.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pl.instagram.instagram.exception.NonLoggedException;
import pl.instagram.instagram.exception.UserIsNotResourceAuthorException;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration
class AuthServiceTest {

    private final AuthService authService = new AuthService();

    @Test
    @WithMockUser
    void shouldCheckIfIsUserLoggedWhenUserIsLogged() {

        boolean actual = authService.isUserLogged();

        assertTrue(actual);
    }

    @Test
    void shouldCheckIfIsNotUserLoggedWhenIsUserNotLogged() {

        boolean actual = authService.isUserLogged();

        assertFalse(actual);
    }

    @Test
    @WithMockUser(username = "kamil")
    void shouldPositivelyCheckLoggedUserResourceAuthorshipWhenUserIsAllowed() {

        UUID existingId = UUID.randomUUID();

        authService.checkLoggedUserResourceAuthorship(
            existingId,
            (id, accountId) -> accountId.equals("kamil") && id.equals(existingId)
        );
    }

    @Test
    void shouldNegativelyCheckLoggedUserResourceAuthorshipWhenUserIsNoLogged() {

        UUID existingId = UUID.randomUUID();

        assertThatThrownBy(() -> authService.checkLoggedUserResourceAuthorship(
            existingId,
            (id, accountId) -> accountId.equals("kamil") && id.equals(existingId))
        )
            .isInstanceOf(NonLoggedException.class);
    }

    @Test
    @WithMockUser
    void shouldNegativelyCheckLoggedUserResourceAuthorshipWhenUserIsNotAllowed() {

        UUID existingId = UUID.randomUUID();

        assertThatThrownBy(() -> authService.checkLoggedUserResourceAuthorship(
            existingId,
            (id, accountId) -> accountId.equals("kamil") && id.equals(existingId))
        )
            .isInstanceOf(UserIsNotResourceAuthorException.class)
            .hasMessage("Użytkownik nie jest autorem podanego zasobu");
    }

    @Test
    @WithMockUser
    void shouldGetLoggedUserDetailsWhenUserIsLogged() {

        assertThat(authService.getLoggedUserDetails()).isNotNull();
    }

    @Test
    void shouldNotGetLoggedUserDetailsWhenUserIsUnLogged() {

        assertThat(authService.getLoggedUserDetails()).isNull();
    }

    @Test
    @WithMockUser(username = "kamil")
    void shouldGetLoggedUserAccountIdWhenUserIsLogged() {

        String actualAccountId = authService.getLoggedUserAccountId();

        assertThat(actualAccountId).isEqualTo("kamil");
    }

    @Test
    void shouldNotGetLoggedUserAccountIdWhenUserIsNotLogged() {

        assertThrows(NonLoggedException.class, authService::getLoggedUserAccountId);
    }
}