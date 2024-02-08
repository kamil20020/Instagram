import React from "react";
import "./Comment.css";
import CommentAPIService, {
  CreateComment,
} from "../../services/CommentAPIService";
import { useDispatch } from "react-redux";
import {
  NotificationType,
  setNotification,
} from "../../redux/slices/notificationSlice";

const CreateCommentView = (props: {
  postId: string;
  parentCommentId?: string;
}) => {
  const [commentContent, setCommentContent] = React.useState<string>("");

  const notificationDispatch = useDispatch();

  const handleCreate = () => {
    let request: CreateComment = {
      userId: "96f2e9fa-d4f0-4067-a272-04cc766f688b",
      content: commentContent,
    };

    if (props.parentCommentId) {
      request.parentCommentId = props.parentCommentId;
    }

    CommentAPIService.createComment(props.postId, request)
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
