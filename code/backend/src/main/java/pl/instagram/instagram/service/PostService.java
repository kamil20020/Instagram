package pl.instagram.instagram.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import pl.instagram.instagram.exception.EntityNotFoundException;
import pl.instagram.instagram.exception.UserIsNotResourceAuthorException;
import pl.instagram.instagram.mapper.PostMapper;
import pl.instagram.instagram.model.domain.PatchPostData;
import pl.instagram.instagram.model.domain.PostEntityForLoggedUser;
import pl.instagram.instagram.model.entity.PostEntity;
import pl.instagram.instagram.model.entity.UserEntity;
import pl.instagram.instagram.repository.PostLikeRepository;
import pl.instagram.instagram.repository.PostRepository;
import pl.instagram.instagram.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostLikeRepository postLikeRepository;

    private final UserService userService;
    private final AuthService authService;

    private final PostMapper postMapper;

    public boolean existsById(UUID id){
        return postRepository.existsById(id);
    }

    public PostEntity getPostById(UUID postId) throws EntityNotFoundException {

       return postRepository.findById(postId)
            .orElseThrow(() ->
                new EntityNotFoundException("Nie istnieje post o takim id")
            );
    }

    public PostEntityForLoggedUser getPostByIdForLoggedUser(UUID postId) throws EntityNotFoundException {

        PostEntity foundPost = getPostById(postId);

        boolean didLoggedUserLikePost = false;

        if(authService.isUserLogged()){

            String loggedUserAccountId = authService.getLoggedUserAccountId();

            didLoggedUserLikePost = postLikeRepository.existsByAuthorAccountIdAndPostId(loggedUserAccountId, postId);
        }

        PostEntityForLoggedUser convertedPost = postMapper.postEntityToPostEntityForLoggedUser(foundPost);
        convertedPost.setDidLoggedUserLikePost(didLoggedUserLikePost);

        return convertedPost;
    }

    public Page<PostEntity> getUserPostsPage(UUID authorId, Pageable pageable) throws IllegalArgumentException, EntityNotFoundException {

        if(pageable == null){
            throw new IllegalArgumentException("Paginacja jest wymagana");
        }

        if(!userService.existsById(authorId)){
            throw new EntityNotFoundException("Nie istnieje użytkownik o takim id");
        }

        pageable = PageRequest.of(
            pageable.getPageNumber(), pageable.getPageSize(), Sort.by("creationDatetime").descending()
        );

        return postRepository.findAllByAuthorId(authorId, pageable);
    }

    public Page<PostEntityForLoggedUser> getPostsFromFollowedUsers(Pageable pageable) throws IllegalArgumentException{

        if(pageable == null){
            throw new IllegalArgumentException("Paginacja jest wymagana");
        }

        pageable = PageRequest.of(
            pageable.getPageNumber(),
            pageable.getPageSize(),
            Sort.by(Sort.Direction.DESC, "creationDatetime")
        );

        UserEntity loggedUser = userService.getLoggedUser();

        Page<PostEntity> gotPosts = postRepository.findByAuthorFollowedUsersFollowerId(loggedUser.getId(), pageable);

        return gotPosts.map(post -> {

            boolean didLoggedUserLikePost = postLikeRepository.existsByAuthorAccountIdAndPostId(loggedUser.getAccountId(), post.getId());

            PostEntityForLoggedUser convertedPost = postMapper.postEntityToPostEntityForLoggedUser(post);
            convertedPost.setDidLoggedUserLikePost(didLoggedUserLikePost);

            return convertedPost;
        });
    }

    @Transactional
    public PostEntity createPost(PostEntity postData) throws EntityNotFoundException{

        UserEntity loggedUser = userService.getLoggedUser();

        PostEntity newPost = PostEntity.builder()
            .creationDatetime(LocalDateTime.now())
            .description(postData.getDescription())
            .content(postData.getContent())
            .areHiddenLikes(postData.isAreHiddenLikes())
            .areDisabledComments(postData.isAreDisabledComments())
            .author(loggedUser)
			.likesCount(0)
			.commentsCount(0)
            .comments(new HashSet<>())
            .postLikes(new HashSet<>())
            .build();

        loggedUser.getPosts().add(newPost);
        loggedUser.setNumberOfPosts(loggedUser.getNumberOfPosts() + 1);

        return postRepository.save(newPost);
    }

    @Transactional
    public PostEntity patchPostById(UUID postId, PatchPostData updateData) throws EntityNotFoundException, UserIsNotResourceAuthorException {

        PostEntity foundPost = getPostById(postId);

        authService.checkLoggedUserResourceAuthorship(foundPost.getAuthor().getAccountId());

        if(updateData.description() != null){
            foundPost.setDescription(updateData.description());
        }

        if(updateData.areHiddenLikes() != null){
            foundPost.setAreHiddenLikes(updateData.areHiddenLikes());
        }

        if(updateData.areDisabledComments() != null){
            foundPost.setAreDisabledComments(updateData.areDisabledComments());
        }

        return foundPost;
    }

    @Transactional
    public void deletePostById(UUID postId) throws EntityNotFoundException, UserIsNotResourceAuthorException {

        PostEntity foundPost = getPostById(postId);
        UserEntity author = foundPost.getAuthor();

        authService.checkLoggedUserResourceAuthorship(postId, postRepository::existsByIdAndAuthorAccountId);

        author.setNumberOfPosts(author.getNumberOfPosts() - 1);
        author.getPosts().remove(foundPost);

        postRepository.deleteById(postId);
    }
}
