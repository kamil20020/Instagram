import React from "react";
import IconWithText from "../../../components/IconWithText";
import OptionsDialog from "../../../components/OptionsDialog";
import { setNotification, NotificationType } from "../../../redux/slices/notificationSlice";
import PostAPIService from "../../../services/PostAPIService";
import { useSelector, useDispatch } from "react-redux";
import { useAuthSelector } from "../../../redux/slices/authSlice";
import { Post } from "../../../models/responses/PostDetails";

const PostActions = (props: {
    post: Post,
    onDelete: (postId: string) => void
}) => {
    const post = props.post
    
    const [showOptions, setShowOptions] = React.useState<boolean>(false);

    const loggedUserId = useSelector(useAuthSelector).user?.id

    const dispatch = useDispatch()

    const handleShowOptions = (isOpen?: boolean) => {
        setShowOptions(isOpen as boolean)
    }

    const handleCloseShowOptions = () => {
        setShowOptions(false)
    }

    const handleDeletePost = () => {
        handleCloseShowOptions()

        PostAPIService.deletePost(post?.id as string)
        .then(() => {
            dispatch(setNotification({
                type: NotificationType.success,
                message: "Usunięto post"
            }))

            props.onDelete(post.id)

            // navigate(`/profile/${loggedUserId}`)
        })
    }

    return (
        <>
            <button className="outlined-button" onClick={() => handleShowOptions(true)}>
                <IconWithText iconName="more_horiz" />
            </button>
            <OptionsDialog 
                isOpen={showOptions}
                setIsOpen={handleShowOptions}
                handleClose={handleCloseShowOptions}
                options={
                loggedUserId === post.author.id ? 
                    [
                    // {name: "Zgłoś", handle: () => console.log("Zgłoś")},
                    // {name: "Edytuj", handle: () => console.log("Edytuj")},
                    {name: "Usuń", handle: handleDeletePost}
                    ]
                :
                    [{name: "Zgłoś", handle: () => console.log("Zgłoś")}]
                }
            />
        </>
    )
}

export default PostActions;