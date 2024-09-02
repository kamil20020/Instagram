import React from "react";
import Avatar from "../../components/Avatar";
import Icon from "../../components/Icon";
import IconWithText from "../../components/IconWithText";
import OptionsDialog from "../../components/OptionsDialog";
import SimpleProfileHeader from "../../components/SimpleProfileHeader";
import { CommentData } from "../../models/responses/CommentData";
import { useDispatch, useSelector } from "react-redux";
import { useAuthSelector } from "../../redux/slices/authSlice";
import { useNavigate } from "react-router-dom";
import { setNotification, NotificationType } from "../../redux/slices/notificationSlice";
import PostAPIService from "../../services/PostAPIService";
import CommentAPIService from "../../services/CommentAPIService";
import { Page } from "../../models/responses/Page";
import { clearComment, commentSelector, focusComment, setParentCommentId } from "../../redux/slices/commentSlice";
import SubCommentsView from "./SubCommentsView";
import CommentLike from "./CommentLike";
import CommentActions from "./CommentActions";
import CommentLikes from "./CommentLikes";

const CommentView = (props: {
  postId: string, 
  comment: CommentData,
  parentCommentId?: string,
  onDelete: (id: string) => void
}) => {
  const comment = props.comment;

  const isLikedComment = React.useRef<boolean>(comment.didLoggedUserLikeComment)

  const dispatch = useDispatch()

  return (
    <div className="comment" style={{marginLeft: props.parentCommentId ? 48 : 0}}>
      <div style={{display: "flex", justifyContent: "space-between"}}>
        <div className="comment-content">
          <Avatar width={40} height={40} image={comment.author.avatar} userId={comment.author.id}/>
          <div className="comment-details">
            <div>
              <h4 style={{ display: "inline-block", marginRight: 8 }}>
                {comment.author.nickname}
              </h4>
              <span>{comment.content}</span>
            </div>
            <div style={{ display: "flex", alignItems: "start" }}>
              <span style={{ color: "#706f6e", marginRight: 8 }}>
                {new Date(comment.creationDatetime).toLocaleDateString()}
              </span>
              <CommentLikes commentId={comment.id} commentLikes={comment.likesCount}/>
              <button className="grey-button-outlined" onClick={() => dispatch(focusComment(comment.id))}>Odpowiedz</button>
              <CommentActions comment={comment} onDelete={props.onDelete}/>
            </div>
          </div>
        </div>
        <CommentLike commentId={comment.id} isLikedComment={isLikedComment.current}/>
      </div>
      {comment.subCommentsCount > 0 && <SubCommentsView postId={props.postId} comment={comment}/>}
    </div>
  );
};

export default CommentView;
