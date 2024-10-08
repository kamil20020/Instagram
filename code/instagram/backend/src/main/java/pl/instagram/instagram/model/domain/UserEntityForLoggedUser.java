package pl.instagram.instagram.model.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import pl.instagram.instagram.model.entity.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEntityForLoggedUser {

    private UUID id;
    private String accountId;
    private String nickname;
    private String firstname;
    private String surname;
    private String tel;
    private byte[] avatar;
    private LocalDateTime creationDatetime;
    private String description;
    private boolean isVerified;
    private boolean isPrivate;
    private Integer followers;
    private Integer followings;
    private Integer numberOfPosts;
    private Set<PostEntity> posts;
    private Set<PostLikeEntity> likedPosts;
    private Set<FollowerEntity> followedUsers;
    private Set<FollowerEntity> followersUsers;
    private Set<CommentLikeEntity> likedComments;
    private Set<CommentEntity> comments;
    private boolean didLoggedUserFollow;
}