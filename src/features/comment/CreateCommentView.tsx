import React from "react";
import "./Comment.css";
import CommentAPIService from "../../services/CommentAPIService";
import { useDispatch, useSelector } from "react-redux";
import {
  NotificationType,
  setNotification,
} from "../../redux/slices/notificationSlice";
import { CreateComment } from "../../models/requests/CreateComment";
import useComponentVisible from "../../components/useComponentVisible";
import { clearComment, commentSelector, finishComment, setCommentContent } from "../../redux/slices/commentSlice";

const CreateCommentView = (props: {
  postId: string;
}) => {

  const comment = useSelector(commentSelector)

  const dispatch = useDispatch();

  const ref = useComponentVisible(() => {
    dispatch(clearComment())
  }) as any;

  const handleCreate = () => {
    let request: CreateComment = {
      content: comment.content,
    };

    CommentAPIService.createComment(props.postId, request, comment.parentCommentId)
    .then((response) => {
      dispatch(finishComment())

      dispatch(
        setNotification({
          message: "Utworzono komentarz",
          type: NotificationType.success,
        })
      );
    });
  };

  const handleChangeCommentContent = (event: any) => {
    const content = event.target.value

    dispatch(
      setCommentContent(content)
    )
  }

  if(!comment.isCreating){
    return <></>
  }

  return (
    <div className="create-comment" ref={ref ? ref : null}>
      <input
        className="comment-input"
        type="text"
        placeholder="Dodaj komentarz..."
        value={comment.content}
        onChange={handleChangeCommentContent}
      />
      <button className="blue-button" autoFocus={comment.isCreating} onClick={handleCreate}>
        Opublikuj
      </button>
    </div>
  );
};

export default CreateCommentView;
