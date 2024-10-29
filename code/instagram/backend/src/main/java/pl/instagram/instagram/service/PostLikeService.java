package pl.instagram.instagram.service;

import jakarta.persistence.EntityExistsException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import pl.instagram.instagram.exception.EntityNotFoundException;
import pl.instagram.instagram.mapper.PostMapper;
import pl.instagram.instagram.model.api.response.PostLikesResponse;
import pl.instagram.instagram.model.domain.PostEntityForLoggedUser;
import pl.instagram.instagram.model.domain.PostLikes;
import pl.instagram.instagram.model.entity.PostEntity;
import pl.instagram.instagram.model.entity.PostLikeEntity;
import pl.instagram.instagram.model.entity.UserEntity;
import pl.instagram.instagram.repository.PostLikeRepository;
import pl.instagram.instagram.repository.UserRepository;

import java.util.UUID;

@Lazy
@Slf4j
@Service
@RequiredArgsConstructor
public class PostLikeService {

    private final PostLikeRepository postLikeRepository;

    private final PostService postService;
    private final UserService userService;
    private final AuthService authService;

    private PostLikeEntity getById(PostLikeEntity.PostLikeEntityId id) throws EntityNotFoundException {

        return postLikeRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Nie istnieje polubienie posta o takim id"));
    }

    public PostLikes getPostLikes(UUID postId, Pageable pageable) throws IllegalArgumentException, EntityNotFoundException {

        if(pageable == null){
            throw new IllegalArgumentException("Paginacja jest wymagana");
        }

        if(!postService.existsById(postId)){
            throw new EntityNotFoundException("Nie istnieje post o takim id");
        }

        pageable = PageRequest.of(
            pageable.getPageNumber(), pageable.getPageSize()
        );

        Page<PostLikeEntity> postLikesPage = postLikeRepository.findByPostId(postId, pageable);
        Page<UserEntity> postLikesAuthorsPage = postLikesPage.map(PostLikeEntity::getAuthor);

        boolean didLoggedUserLikePost = false;

        if(authService.isUserLogged()){

            String loggedUserAccountId = authService.getLoggedUserAccountId();

            didLoggedUserLikePost = postLikeRepository.existsByAuthorAccountIdAndPostId(loggedUserAccountId, postId);
        }

        return new PostLikes(postId, postLikesAuthorsPage, didLoggedUserLikePost);
    }

    @Transactional
    public UserEntity addLike(UUID postId) throws EntityNotFoundException, EntityExistsException {

        PostEntity post = postService.getPostById(postId);
        UserEntity loggedUser = userService.getLoggedUser();

        if(postLikeRepository.existsByAuthorAccountIdAndPostId(loggedUser.getAccountId(), postId)){
            throw new EntityExistsException("Istnieje już polubienie posta o takich id posta i konta użytkownika");
        }

        PostLikeEntity toCreatePostLike = new PostLikeEntity(loggedUser, post);
        PostLikeEntity createdPostLike = postLikeRepository.save(toCreatePostLike);

        post.getPostLikes().add(createdPostLike);
        post.setLikesCount(post.getLikesCount() + 1);

        loggedUser.getLikedPosts().add(createdPostLike);

        return loggedUser;
    }

    @Transactional
    public void removeLike(UUID postId) throws EntityNotFoundException {

        PostEntity post = postService.getPostById(postId);
        UserEntity loggedUser = userService.getLoggedUser();

        PostLikeEntity.PostLikeEntityId postLikeId = new PostLikeEntity.PostLikeEntityId(loggedUser.getId(), postId);

        PostLikeEntity foundPostLike = getById(postLikeId);

        post.getPostLikes().remove(foundPostLike);
        post.setLikesCount(post.getLikesCount() - 1);

        loggedUser.getLikedPosts().remove(foundPostLike);

        postLikeRepository.deleteById(postLikeId);
    }
}
