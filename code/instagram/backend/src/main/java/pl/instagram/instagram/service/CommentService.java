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
import pl.instagram.instagram.mapper.CommentMapper;
import pl.instagram.instagram.mapper.UserMapper;
import pl.instagram.instagram.model.domain.CommentEntityForLoggedUser;
import pl.instagram.instagram.model.entity.CommentEntity;
import pl.instagram.instagram.model.entity.PostEntity;
import pl.instagram.instagram.model.entity.UserEntity;
import pl.instagram.instagram.repository.CommentLikeRepository;
import pl.instagram.instagram.repository.CommentRepository;
import pl.instagram.instagram.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;

    private final PostService postService;
    private final UserService userService;
    private final AuthService authService;

    private final CommentMapper commentMapper;

    public CommentEntity getById(UUID commentId) throws EntityNotFoundException{
        return commentRepository.findById(commentId)
            .orElseThrow(() -> new EntityNotFoundException("Nie istnieje komentarz o takim id"));
    }

    public Page<CommentEntityForLoggedUser> getPostCommentsPage(UUID postId, UUID parentCommentId, Pageable pageable) throws IllegalArgumentException, EntityNotFoundException {

        if(pageable == null){
            throw new IllegalArgumentException("Paginacja jest wymagana");
        }

        if(!postService.existsById(postId)){
            throw new EntityNotFoundException("Nie istnieje post o takim id");
        }

        if(parentCommentId != null && !commentRepository.existsById(parentCommentId)){
            throw new EntityNotFoundException("Nie istnieje nadrzędny komentarz o takim id");
        }

        pageable = PageRequest.of(
            pageable.getPageNumber(), pageable.getPageSize(), Sort.by("creationDatetime").descending()
        );

        Page<CommentEntity> gotCommentsPage = commentRepository.getAllByPostIdAndParentCommentId(postId, parentCommentId, pageable);

        return appendLoggedUserInfo(gotCommentsPage);
    }

    public Page<CommentEntityForLoggedUser> appendLoggedUserInfo(Page<CommentEntity> commentsPage){

        Page<CommentEntityForLoggedUser> commentsForLoggedUserPage;

        if(authService.isUserLogged()){

            String loggedUserAccountId = authService.getLoggedUserAccountId();

            commentsForLoggedUserPage = commentsPage
                .map(comment -> {
                    boolean didLoggedUserLikeComment = commentLikeRepository.existsByAuthorAccountIdAndCommentId(
                        loggedUserAccountId, comment.getId()
                    );

                    CommentEntityForLoggedUser convertedComment = commentMapper.commentEntityToCommentEntityForLoggedUser(comment);
                    convertedComment.setDidLoggedUserLikeComment(didLoggedUserLikeComment);

                    return convertedComment;
                });
        }
        else{
            commentsForLoggedUserPage = commentsPage.map(commentMapper::commentEntityToCommentEntityForLoggedUser);
        }

        return commentsForLoggedUserPage;
    }

    @Transactional
    public CommentEntity createComment(UUID postId, Optional<UUID> parentCommentIdOpt, String content) throws EntityNotFoundException {

        PostEntity post = postService.getPostById(postId);
        UserEntity loggedUser = userService.getLoggedUser();

        CommentEntity newComment = CommentEntity.builder()
            .content(content)
            .author(loggedUser)
            .post(post)
            .creationDatetime(LocalDateTime.now())
			.likesCount(0)
			.subCommentsCount(0)
            .subComments(new HashSet<>())
            .commentLikes(new HashSet<>())
            .build();

        CommentEntity createdComment;

        if(parentCommentIdOpt.isPresent()) {

            UUID parentCommentId = parentCommentIdOpt.get();

            CommentEntity parentComment = commentRepository.findById(parentCommentId)
                .orElseThrow(() ->
                    new EntityNotFoundException("Nie istnieje nadrzędny komentarz o takim id")
                );

            newComment.setParentComment(parentComment);

            createdComment = commentRepository.save(newComment);
            parentComment.getSubComments().add(createdComment);
            parentComment.setSubCommentsCount(parentComment.getSubCommentsCount() + 1);
        }
        else{
            createdComment = commentRepository.save(newComment);
        }

        loggedUser.getComments().add(createdComment);
        post.getComments().add(createdComment);
        post.setCommentsCount(post.getCommentsCount() + 1);

        return createdComment;
    }

    @Transactional
    public CommentEntity updateComment(UUID commentId, String newContent) throws EntityNotFoundException, UserIsNotResourceAuthorException {

        CommentEntity foundComment = getById(commentId);

        authService.checkLoggedUserResourceAuthorship(foundComment.getAuthor().getAccountId());

        foundComment.setContent(newContent);

        return foundComment;
    }

    @Transactional
    public void deleteComment(UUID commentId) throws EntityNotFoundException, UserIsNotResourceAuthorException{

        CommentEntity foundComment = getById(commentId);
        PostEntity foundPost = foundComment.getPost();
        UserEntity foundAuthor = foundComment.getAuthor();

        authService.checkLoggedUserResourceAuthorship(commentId, commentRepository::existsByIdAndAuthor_AccountId);

        if(foundComment.getParentComment() != null) {

            CommentEntity parentComment = foundComment.getParentComment();
            parentComment.setSubCommentsCount(parentComment.getSubCommentsCount() - 1);
            parentComment.getSubComments().remove(foundComment);
        }

        foundPost.setCommentsCount(foundPost.getCommentsCount() - 1);
        foundPost.getComments().remove(foundComment);
        foundAuthor.getComments().remove(foundComment);

        commentRepository.deleteById(commentId);
    }
}
