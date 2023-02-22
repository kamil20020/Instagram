package pl.instagram.instagram.config;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.instagram.instagram.model.entity.UserEntity;
import pl.instagram.instagram.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;

@Slf4j
@Configuration
public class LoadDatabase {

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository) {

        return args -> {
            UserEntity u1 = UserEntity.builder()
                .userId(1)
                .userAccountId("1")
                .nickname("kamil_dywan")
                .firstname("Kamil")
                .surname("Dywan")
                .email("kamil@wp.pl")
                .creationDatetime(LocalDateTime.now())
                .build();

            UserEntity u2 = UserEntity.builder()
                    .userId(2)
                    .userAccountId("2")
                    .nickname("michal_nowak")
                    .firstname("Michał")
                    .surname("Nowak")
                    .email("michal@wp.pl")
                    .creationDatetime(LocalDateTime.now())
                    .build();

            log.info("Preloading " + userRepository.save(u1));
            log.info("Preloading " + userRepository.save(u2));
        };
    }
}
