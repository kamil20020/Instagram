package pl.instagram.instagram.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import pl.instagram.instagram.exception.EntityNotFoundException;
import pl.instagram.instagram.model.entity.CommentEntity;
import pl.instagram.instagram.model.entity.PostEntity;
import pl.instagram.instagram.model.entity.UserEntity;
import pl.instagram.instagram.repository.CommentRepository;
import pl.instagram.instagram.repository.PostRepository;
import pl.instagram.instagram.repository.UserRepository;
import pl.instagram.instagram.service.CommentService;
import pl.instagram.instagram.service.PostService;
import pl.instagram.instagram.service.UserService;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final PostService postService;
    private final UserService userService;

    @Override
    public Page<CommentEntity> getPostCommentsPage(UUID postId, Pageable pageable) throws EntityNotFoundException {

        pageable = PageRequest.of(
            pageable.getPageNumber(), pageable.getPageSize(), Sort.by("creationDatetime").descending()
        );

        if(!postRepository.existsById(postId)){
            throw new EntityNotFoundException("Nie istnieje post o takim id");
        }

        return commentRepository.getAllByPostEntityId(postId, pageable);
    }

    @Override
    public Page<CommentEntity> getSubCommentsPage(UUID parentCommentId, Pageable pageable) throws EntityNotFoundException {

        if(!postRepository.existsById(parentCommentId)){
            throw new EntityNotFoundException("Nie istnieje nadrzędny komentarz o takim id");
        }

        return commentRepository.getAllByParentCommentId(parentCommentId, pageable);
    }

    @Override
    public CommentEntity createComment(UUID postId, UUID userId, UUID parentCommentId, String content) throws EntityNotFoundException {

        PostEntity postEntity = postService.getPostById(postId);
        UserEntity userEntity = userService.getUserById(userId);

        CommentEntity toSaveComment =  CommentEntity.builder()
            .content(content)
            .postEntity(postEntity)
            .userEntity(userEntity)
            .creationDatetime(LocalDateTime.now())
            .build();

        if(parentCommentId != null) {
            toSaveComment.setParentComment(
                commentRepository.findById(parentCommentId).orElseThrow(() ->
                    new EntityNotFoundException("Nie istnieje nadrzędny komentarz o takim id")
                )
            );
        }

        return commentRepository.save(toSaveComment);
    }

    @Override
    public CommentEntity updateComment(UUID commentId, String newContent) throws EntityNotFoundException {

        CommentEntity foundComment = commentRepository.findById(commentId).orElseThrow(() ->
            new EntityNotFoundException("Nie istnieje komentarz o takim id")
        );

        foundComment.setContent(newContent);

        return foundComment;
    }

    @Override
    public void deleteComment(UUID commentId) throws EntityNotFoundException{

        if(!commentRepository.existsById(commentId)){
            throw new EntityNotFoundException("Nie istnieje komentarz o takim id");
        }

        commentRepository.deleteById(commentId);
    }
}
