package pl.instagram.instagram.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name="POSTS_LIKES")
public class PostLike {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "post_like_id", insertable = false, updatable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity userEntity;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private PostEntity postEntity;
}
