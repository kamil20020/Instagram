import React from "react";
import CommentAPIService from "../../../services/CommentAPIService";
import { Pagination } from "../../../models/requests/Pagination";
import { Page } from "../../../models/responses/Page";
import { CommentData } from "../../../models/responses/CommentData";
import CommentView from "../../comment/CommentView";
import { Post } from "../../../models/responses/PostDetails";
import Avatar from "../../../components/Avatar";
import PostDescription from "./PostDescription";

const PostComments = (props: { post: Post }) => {
  const post = props.post;

  const [comments, setComments] = React.useState<CommentData[]>([]);
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
      {!post.areDisabledComments ?
          comments.map((comment: CommentData) => (
            <CommentView key={comment.id} comment={comment} />
          ))
        :
          <h4>Ukryto komentarze</h4>
      }
    </div>
  );
};

export default PostComments;
