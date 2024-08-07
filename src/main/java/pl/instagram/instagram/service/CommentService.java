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
import pl.instagram.instagram.model.entity.CommentEntity;
import pl.instagram.instagram.model.entity.PostEntity;
import pl.instagram.instagram.model.entity.UserEntity;
import pl.instagram.instagram.repository.CommentRepository;
import pl.instagram.instagram.repository.PostRepository;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostService postService;
    private final UserService userService;

    public CommentEntity getById(UUID commentId) throws EntityNotFoundException{
        return commentRepository.findById(commentId)
            .orElseThrow(() -> new EntityNotFoundException("Nie istnieje komentarz o takim id"));
    }

    public Page<CommentEntity> getPostCommentsPage(UUID postId, Pageable pageable) throws EntityNotFoundException {

        if(!postService.existsById(postId)){
            throw new EntityNotFoundException("Nie istnieje post o takim id");
        }

        pageable = PageRequest.of(
            pageable.getPageNumber(), pageable.getPageSize(), Sort.by("creationDatetime").descending()
        );

        return commentRepository.getAllByPostIdAndParentCommentIsNull(postId, pageable);
    }

    public Page<CommentEntity> getSubCommentsPage(UUID parentCommentId, Pageable pageable) throws EntityNotFoundException {

        if(!commentRepository.existsById(parentCommentId)){
            throw new EntityNotFoundException("Nie istnieje nadrzędny komentarz o takim id");
        }

        pageable = PageRequest.of(
            pageable.getPageNumber(), pageable.getPageSize(), Sort.by("creationDatetime").ascending()
        );

        return commentRepository.getAllByParentCommentId(parentCommentId, pageable);
    }

    @Transactional
    public CommentEntity createComment(UUID postId, UUID authorId, UUID parentCommentId, String content)
        throws EntityNotFoundException
    {
        PostEntity post = postService.getPostById(postId);
        UserEntity author = userService.getUserById(authorId);

        CommentEntity newComment = CommentEntity.builder()
            .content(content)
            .author(author)
            .post(post)
            .creationDatetime(LocalDateTime.now())
            .build();

        CommentEntity createdComment;

        if(parentCommentId != null) {

            CommentEntity parentComment = commentRepository.findById(parentCommentId)
                .orElseThrow(() ->
                    new EntityNotFoundException("Nie istnieje nadrzędny komentarz o takim id")
                );

            newComment.setParentComment(parentComment);

            createdComment = commentRepository.save(newComment);

            parentComment.getSubComments().add(createdComment);
        }
        else{
            createdComment = commentRepository.save(newComment);
        }

        author.getComments().add(createdComment);
        post.getComments().add(createdComment);

        return createdComment;
    }

    @Transactional
    public CommentEntity updateComment(UUID commentId, String newContent) throws EntityNotFoundException {

        CommentEntity foundComment = getById(commentId);

        foundComment.setContent(newContent);

        return foundComment;
    }

    public void deleteComment(UUID commentId) throws EntityNotFoundException{

        if(!commentRepository.existsById(commentId)){
            throw new EntityNotFoundException("Nie istnieje komentarz o takim id");
        }

        commentRepository.deleteById(commentId);
    }
}
