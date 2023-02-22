package pl.instagram.instagram.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name="USERS")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="user_id", insertable = false, updatable = false)
    private Integer userId;

    @Column(name = "user_account_id", nullable = false, unique = true)
    private String userAccountId;

    @Column(name = "nickname", nullable = false, unique = true)
    private String nickname;

    @Column(name = "firstname", nullable = false)
    private String firstname;

    @Column(name = "surname", nullable = false)
    private String surname;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "tel")
    private String tel;

    @Column(name = "avatar")
    private Byte[] avatar;

    @Column(name = "creation_datetime", nullable = false)
    private LocalDateTime creationDatetime;
}
