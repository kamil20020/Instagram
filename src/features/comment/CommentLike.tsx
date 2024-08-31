import React from "react";
import Icon from "../../components/Icon";
import CommentAPIService from "../../services/CommentAPIService";
import { useDispatch } from "react-redux";
import { setNotification, NotificationType } from "../../redux/slices/notificationSlice";
import PostLikeAPIService from "../../services/PostLikeAPIService";
import CommentLikeAPIService from "../../services/CommentLikeAPIService";

const CommentLike = (props: {
    commentId: string,
    isLikedComment: boolean;
}) => {
    const [isLikedComment, setIsLikedComment] = React.useState<boolean>(props.isLikedComment)

    const dispatch = useDispatch()

    const handleSwitchCommentLike = () => {

        if(props.isLikedComment){
            CommentLikeAPIService.delete(props.commentId)
            .then(() => {
                dispatch(setNotification({
                    type: NotificationType.success,
                    message: "Usunięto polubienie"
                }))
        
                setIsLikedComment(false)
            })
        }
        else{
            CommentLikeAPIService.create(props.commentId)
            .then(() => {
                dispatch(setNotification({
                    type: NotificationType.success,
                    message: "Dodano polubienie"
                }))
        
                setIsLikedComment(true)
            });
        }
    }

    return (
        <button
            className="grey-button-outlined" 
            style={{marginTop: 8}}
            onClick={handleSwitchCommentLike}
        >
        <Icon iconName="favorite" iconStyle={{color: isLikedComment ? "red" : "black", fontSize: 28}}/>
      </button>
    );
}

export default CommentLike;