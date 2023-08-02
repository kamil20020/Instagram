package pl.instagram.instagram.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.instagram.instagram.exception.EntityNotFoundException;
import pl.instagram.instagram.model.entity.PostEntity;
import pl.instagram.instagram.model.entity.UserEntity;
import pl.instagram.instagram.repository.PostRepository;
import pl.instagram.instagram.repository.UserRepository;
import pl.instagram.instagram.service.PostService;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Override
    public PostEntity getPostById(UUID postId) throws EntityNotFoundException {

        return postRepository.findById(postId).orElseThrow(() ->
            new EntityNotFoundException("Nie istnieje post o takim id")
        );
    }

    @Override
    public Page<PostEntity> getUserPostsPage(UUID userId, Pageable pageable) throws EntityNotFoundException {

        if(!userRepository.existsById(userId)){
            throw new EntityNotFoundException("Nie istnieje użytkownik o takim id");
        }

        return postRepository.findAllByUserEntityUserId(userId, pageable);
    }

    @Override
    public PostEntity createPost(UUID userId, PostEntity postEntity) throws EntityNotFoundException{

        UserEntity foundUserEntity = userRepository.findById(userId).orElseThrow(() ->
            new EntityNotFoundException("Nie istnieje użytkownik o takim id")
        );

        PostEntity toCreatePostEntity = PostEntity.builder()
            .creationDatetime(LocalDateTime.now())
            .description(postEntity.getDescription())
            .img(postEntity.getImg())
            .areHiddenLikes(postEntity.isAreHiddenLikes())
            .areDisabledComments(postEntity.isAreDisabledComments())
            .userEntity(foundUserEntity)
            .build();

        return postRepository.save(toCreatePostEntity);
    }

    @Transactional
    @Override
    public void updatePostById(UUID postId, PostEntity postEntity) throws EntityNotFoundException {

        PostEntity foundPostEntity = postRepository.findById(postId).orElseThrow(() ->
            new EntityNotFoundException("Nie istnieje post o takim id")
        );

        if(postEntity.getDescription() != null){
            foundPostEntity.setDescription(postEntity.getDescription());
        }

        if(postEntity.getImg() != null){
            foundPostEntity.setImg(postEntity.getImg());
        }

        foundPostEntity.setAreHiddenLikes(postEntity.isAreHiddenLikes());
        foundPostEntity.setAreDisabledComments(postEntity.isAreDisabledComments());
    }

    @Override
    public void deletePostById(UUID postId) throws EntityNotFoundException {

        if(!postRepository.existsById(postId)){
            throw new EntityNotFoundException("Nie istnieje post o takim id");
        }

        postRepository.deleteById(postId);
    }
}
