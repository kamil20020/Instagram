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

const CommentView = (props: {
  postId: string, 
  comment: CommentData,
  parentCommentId?: string,
  onDelete: (id: string) => void
}) => {
  const comment = props.comment;

  const [showOptions, setShowOptions] = React.useState<boolean>(false);

  const loggedUserId = useSelector(useAuthSelector).user?.id

  const dispatch = useDispatch()

  const handleShowOptions = (isOpen?: boolean) => {
    setShowOptions(isOpen as boolean)
  }

  const handleCloseShowOptions = () => {
    setShowOptions(false)
  }

  const handleDeleteComment = () => {
    handleCloseShowOptions()

    CommentAPIService.deleteComment(comment?.id as string)
    .then(() => {
      dispatch(setNotification({
        type: NotificationType.success,
        message: "Usunięto komentarz"
      }))

      props.onDelete(comment.id)
    })
  }

  return (
    <div className="comment" style={{marginLeft: props.parentCommentId ? 48 : 0}}>
      <div className="comment-content">
        <Avatar width={40} height={40} image={comment.author.avatar} />
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
            <button className="grey-button-outlined">6 polubień</button>
            <button className="grey-button-outlined" onClick={() => dispatch(focusComment(comment.id))}>Odpowiedz</button>
            <button className="grey-button-outlined show-on-hover" onClick={() => handleShowOptions(true)}>
              <Icon
                iconName="more_horiz"
                iconStyle={{ margin: 0, fontSize: 32, fontWeight: "bold", marginTop: -6 }}
              />
            </button>
            <OptionsDialog 
              isOpen={showOptions}
              setIsOpen={handleShowOptions}
              handleClose={handleCloseShowOptions}
              options={
                loggedUserId === comment.author.id ? 
                  [
                    {name: "Zgłoś", handle: () => console.log("Zgłoś")},
                    {name: "Edytuj", handle: () => console.log("Edytuj")},
                    {name: "Usuń", handle: handleDeleteComment}
                  ]
                :
                  [{name: "Zgłoś", handle: () => console.log("Zgłoś")}]
              }
            />
          </div>
        </div>
      </div>
      {comment.subCommentsCount > 0 && <SubCommentsView postId={props.postId} comment={comment}/>}
    </div>
  );
};

export default CommentView;
