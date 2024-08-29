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
import pl.instagram.instagram.model.entity.UserEntity;
import pl.instagram.instagram.repository.UserRepository;

import java.util.UUID;

@Lazy
@Slf4j
@Service
@RequiredArgsConstructor
public class PostLikeService {

    private final UserRepository userRepository;

    private final PostService postService;
    private final UserService userService;
    private final AuthService authService;

    public PostLikes getPostLikes(UUID postId, Pageable pageable) throws IllegalArgumentException, EntityNotFoundException {

        if(pageable == null){
            throw new IllegalArgumentException("Paginacja jest wymagana");
        }

        if(!postService.existsById(postId)){
            throw new EntityNotFoundException("Nie istnieje post o takim id");
        }

        pageable = PageRequest.of(
            pageable.getPageNumber(), pageable.getPageSize(), Sort.by("creationDatetime").ascending()
        );

        Page<UserEntity> postLikesPage = userRepository.findByLikedPostsId(postId, pageable);

        boolean didLoggedUserLikePost = false;

        if(authService.isUserLogged()){

            String loggedUserAccountId = authService.getLoggedUserAccountId();

            didLoggedUserLikePost = userRepository.existsByAccountIdAndLikedPostsId(loggedUserAccountId, postId);
        }

        return new PostLikes(postId, postLikesPage, didLoggedUserLikePost);
    }

    @Transactional
    public UserEntity addLike(UUID postId) throws EntityNotFoundException, EntityExistsException {

        PostEntity post = postService.getPostById(postId);
        UserEntity loggedUser = userService.getLoggedUser();

        if(userRepository.existsByIdAndLikedPostsId(loggedUser.getId(), postId)){
            throw new EntityExistsException("Istnieje już polubienie posta o takich id posta i użytkownika");
        }

        post.setLikesCount(post.getLikesCount() + 1);
        loggedUser.getLikedPosts().add(post);

        return loggedUser;
    }

    @Transactional
    public void removeLike(UUID postId) throws EntityNotFoundException {

        PostEntity post = postService.getPostById(postId);
        UserEntity loggedUser = userService.getLoggedUser();

        if(!userRepository.existsByIdAndLikedPostsId(loggedUser.getId(), postId)){
            throw new EntityNotFoundException("Nie istnieje polubienie posta o takich id użytkownika i posta");
        }

        post.setLikesCount(post.getLikesCount() - 1);
        loggedUser.getLikedPosts().remove(post);
    }
}
