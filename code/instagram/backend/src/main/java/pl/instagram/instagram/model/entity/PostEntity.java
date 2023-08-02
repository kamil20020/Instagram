package pl.instagram.instagram.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name="POSTS")
public class PostEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name="post_id", insertable = false, updatable = false)
    private UUID postId;

    @Column(name = "creation_datetime", nullable = false)
    private LocalDateTime creationDatetime;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "img")
    private byte[] img;

    @Column(name = "are_hidden_likes", nullable = false)
    private boolean areHiddenLikes = false;

    @Column(name = "are_disabled_comments", nullable = false)
    private boolean areDisabledComments = false;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity userEntity;
}
