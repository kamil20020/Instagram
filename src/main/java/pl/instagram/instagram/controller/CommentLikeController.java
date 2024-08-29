package pl.instagram.instagram.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.instagram.instagram.mapper.CommentMapper;
import pl.instagram.instagram.mapper.UUIDMapper;
import pl.instagram.instagram.service.CommentLikeService;

@RestController
@CrossOrigin(value = "*")
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentLikeController {

    private final CommentLikeService commentLikeService;

    private final UUIDMapper uuidMapper;
    private final CommentMapper commentMapper;
}
