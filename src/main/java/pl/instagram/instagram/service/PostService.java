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
import pl.instagram.instagram.model.entity.PostEntity;
import pl.instagram.instagram.model.entity.UserEntity;
import pl.instagram.instagram.repository.PostRepository;
import pl.instagram.instagram.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserService userService;

    public boolean existsById(UUID id){
        return postRepository.existsById(id);
    }

    public PostEntity getPostById(UUID postId) throws EntityNotFoundException {

        return postRepository.findById(postId)
            .orElseThrow(() ->
                new EntityNotFoundException("Nie istnieje post o takim id")
            );
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

    @Transactional
    public PostEntity createPost(UUID authorId, PostEntity postData) throws EntityNotFoundException{

        UserEntity author = userService.getUserById(authorId);

        PostEntity newPost = PostEntity.builder()
            .creationDatetime(LocalDateTime.now())
            .description(postData.getDescription())
            .content(postData.getContent())
            .areHiddenLikes(postData.isAreHiddenLikes())
            .areDisabledComments(postData.isAreDisabledComments())
            .author(author)
            .build();

        author.getPosts().add(newPost);

        return postRepository.save(newPost);
    }


    @Transactional
    public void patchPostById(UUID postId, PostEntity updateData) throws EntityNotFoundException {

        PostEntity foundPost = getPostById(postId);

        if(updateData.getDescription() != null){
            foundPost.setDescription(updateData.getDescription());
        }

        if(updateData.isAreHiddenLikes()){
            updateData.setAreHiddenLikes(updateData.isAreHiddenLikes());
        }

        if(updateData.isAreDisabledComments()){
            updateData.setAreDisabledComments(updateData.isAreDisabledComments());
        }
    }

    public void deletePostById(UUID postId) throws EntityNotFoundException {

        if(!postRepository.existsById(postId)){
            throw new EntityNotFoundException("Nie istnieje post o takim id");
        }

        postRepository.deleteById(postId);
    }
}
