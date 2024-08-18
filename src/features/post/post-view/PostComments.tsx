﻿import React from "react";
import CommentAPIService from "../../../services/CommentAPIService";
import { Pagination } from "../../../models/requests/Pagination";
import { Page } from "../../../models/responses/Page";
import { Comment } from "../../../models/responses/CommentData";
import CommentView from "../../comment/CommentView";
import { Post } from "../../../models/responses/PostDetails";
import Avatar from "../../../components/Avatar";
import PostDescription from "./PostDescription";

const PostComments = (props: { post: Post }) => {
  const post = props.post;

  const [comments, setComments] = React.useState<Comment[]>([]);
  const [page, setPage] = React.useState<number>(0);
  const pageSize = 12;
  let numberOfPages = 0;

  React.useEffect(() => {
    const pagination: Pagination = {
      page: page,
      size: pageSize,
    };

    CommentAPIService.getCommentsPage(post.id, pagination)
    .then((response) => {
      const pagedResponse: Page = response.data;
      setPage(pagedResponse.page);
      setComments(pagedResponse.content);
      numberOfPages = pagedResponse.totalPages;

      console.log(response.data);
    });
  }, []);

  return (
    <div className="post-comments">
      <PostDescription post={post}/>
      {comments.map((comment: Comment) => (
        <CommentView key={comment.id} comment={comment} />
      ))}
    </div>
  );
};

export default PostComments;
