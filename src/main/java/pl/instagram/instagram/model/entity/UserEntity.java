package pl.instagram.instagram.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(
    name="USERS",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_users_account_id", columnNames = "account_id"),
        @UniqueConstraint(name = "uq_users_nickname", columnNames = "nickname")
    }
)
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name="user_id", updatable = false)
    private UUID Id;

    @Column(name = "account_id", nullable = false)
    private String accountId;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "firstname")
    private String firstname;

    @Column(name = "surname")
    private String surname;

    @Column(name = "tel")
    private String tel;

    @Column(name = "avatar")
    private byte[] avatar;

    @CreationTimestamp
    @Column(name = "creation_datetime", nullable = false)
    private LocalDateTime creationDatetime;

    @Column(name = "description")
    private String description;

    @Column(name = "is_verified", nullable = false)
    private boolean isVerified = false;

    @Column(name = "is_private", nullable = false)
    private boolean isPrivate = false;

    @Column(name = "followers", nullable = false)
    private Integer followers = 0;

    @Column(name = "followings", nullable = false)
    private Integer followings = 0;

    @Column(name = "number_of_posts", nullable = false)
    private Integer numberOfPosts = 0;

    @JsonIgnore
    @OneToMany(mappedBy = "author", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<PostEntity> posts;

    @JsonIgnore
    @OneToMany(mappedBy = "author", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<CommentEntity> comments;
}
