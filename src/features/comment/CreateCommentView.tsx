import React from "react";
import "./Comment.css";
import CommentAPIService from "../../services/CommentAPIService";
import { useDispatch } from "react-redux";
import {
  NotificationType,
  setNotification,
} from "../../redux/slices/notificationSlice";
import { CreateComment } from "../../models/requests/CreateComment";

const CreateCommentView = (props: {
  postId: string;
  parentCommentId?: string;
}) => {
  const [commentContent, setCommentContent] = React.useState<string>("");

  const notificationDispatch = useDispatch();

  const handleCreate = () => {
    let request: CreateComment = {
      content: commentContent,
    };

    CommentAPIService.createComment(props.postId, request, props.parentCommentId)
    .then((response) => {
      setCommentContent("");
      notificationDispatch(
        setNotification({
          message: "Utworzono komentarz",
          type: NotificationType.success,
        })
      );
    });
  };

  return (
    <div className="create-comment">
      <input
        className="comment-input"
        type="text"
        placeholder="Dodaj komentarz..."
        value={commentContent}
        onChange={(event: any) => setCommentContent(event.target.value)}
      />
      <button className="blue-button" onClick={handleCreate}>
        Opublikuj
      </button>
    </div>
  );
};

export default CreateCommentView;
