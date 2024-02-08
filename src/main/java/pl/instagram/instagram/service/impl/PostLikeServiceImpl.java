package pl.instagram.instagram.service.impl;

import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.instagram.instagram.exception.EntityNotFoundException;
import pl.instagram.instagram.model.entity.PostEntity;
import pl.instagram.instagram.model.entity.PostLike;
import pl.instagram.instagram.model.entity.UserEntity;
import pl.instagram.instagram.repository.PostLikeRepository;
import pl.instagram.instagram.repository.PostRepository;
import pl.instagram.instagram.service.PostLikeService;
import pl.instagram.instagram.service.PostService;
import pl.instagram.instagram.service.UserService;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostLikeServiceImpl implements PostLikeService {

    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final PostService postService;
    private final UserService userService;

    @Override
    public Page<PostLike> getPostLikes(UUID postId, Pageable pageable) throws EntityNotFoundException {

        if(!postRepository.existsById(postId)){
            throw new EntityNotFoundException("Nie istnieje post o takim id");
        }

        return postLikeRepository.getAllByPostEntityId(postId, pageable);
    }

    @Override
    public PostLike addLike(UUID postId, UUID userId) throws EntityExistsException {

        if(postLikeRepository.existsByUserEntityIdAndPostEntityId(postId, userId)){
            throw new EntityExistsException("Istnieje już taki like");
        }

        PostEntity postEntity = postService.getPostById(postId);
        UserEntity userEntity = userService.getUserById(userId);

        return postLikeRepository.save(
            PostLike.builder()
                .userEntity(userEntity)
                .postEntity(postEntity)
                .build()
        );
    }

    @Override
    public void removeLike(UUID likeId) {

        if(!postLikeRepository.existsById(likeId)){
            throw new EntityNotFoundException("Nie istnieje like o takim id");
        }

        postLikeRepository.deleteById(likeId);
    }
}
