import React from "react";
import { useDispatch, useSelector } from "react-redux";
import Icon from "../../components/Icon";
import OptionsDialog from "../../components/OptionsDialog";
import { CommentData } from "../../models/responses/CommentData";
import { useAuthSelector } from "../../redux/slices/authSlice";
import { setNotification, NotificationType } from "../../redux/slices/notificationSlice";
import CommentAPIService from "../../services/CommentAPIService";

const CommentActions = (props: {
    comment: CommentData,
    onDelete: (commentId: string) => void;
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
        <>
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
                        // {name: "Zgłoś", handle: () => console.log("Zgłoś")},
                        // {name: "Edytuj", handle: () => console.log("Edytuj")},
                        {name: "Usuń", handle: handleDeleteComment}
                        ]
                    :
                        []// [{name: "Zgłoś", handle: () => console.log("Zgłoś")}]
                }
            />
        </>
    );
}

export default CommentActions;