import React from "react";
import CommentAPIService from "../../../services/CommentAPIService";
import { Pagination } from "../../../models/requests/Pagination";
import { Page } from "../../../models/responses/Page";
import { CommentData } from "../../../models/responses/CommentData";
import CommentView from "../../comment/CommentView";
import { Post } from "../../../models/responses/PostDetails";
import Avatar from "../../../components/Avatar";
import PostDescription from "./PostDescription";
import { useDispatch, useSelector } from "react-redux";
import { clearComment, commentSelector } from "../../../redux/slices/commentSlice";

const PostComments = (props: { post: Post }) => {
  const post = props.post;

  const [comments, setComments] = React.useState<CommentData[]>([]);
  const [page, setPage] = React.useState<number>(0);
  const [totalPages, setTotalPages] = React.useState<number>(0);
  const pageSize = 12;

  const commentState = useSelector(commentSelector)

  const dispatch = useDispatch()

  const onDelete = (commentId: string) => {
    setComments(
      comments.filter((comment: CommentData) => comment.id !== commentId)
    )
  }

  const handleShowComments = (newPage: number, doAppend: boolean = true) => {
    const pagination: Pagination = {
      page: newPage,
      size: pageSize,
    };

    CommentAPIService.getCommentsPage(post.id, pagination)
    .then((response) => {
      const pagedResponse: Page = response.data;
      setPage(newPage);
      
      if(doAppend){
        setComments([...comments, ...pagedResponse.content]);
      }
      else{
        setComments(pagedResponse.content)
      }
      
      setTotalPages(pagedResponse.totalPages)

      console.log(response.data);
    });
  }

  React.useEffect(() => {
    handleShowComments(page)
  }, []);

  if(commentState.parentCommentId === undefined && !commentState.isCreating && commentState.content){
    handleShowComments(0, false)
    setPage(0)
    dispatch(clearComment())
  }

  return (
    <div className="post-comments">
      <PostDescription post={post}/>
      {!post.areDisabledComments ?
          comments.map((comment: CommentData) => (
            <CommentView 
              key={comment.id} 
              postId={post.id} 
              comment={comment}
              onDelete={onDelete}
            />
          ))
        :
          <h4>Ukryto komentarze</h4>
      }
      {page < (totalPages - 1) &&
        <div className="load-more-comments" style={{display: "flex", justifyContent: "center"}}>
          <button 
            className="grey-button"
            onClick={() => handleShowComments(page + 1)} 
          >
            Doładuj komentarze
          </button>
        </div>
      }
    </div>
  );
};

export default PostComments;
