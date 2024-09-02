import { useAuth0 } from "@auth0/auth0-react";
import React, { useEffect } from "react";
import { useDispatch } from "react-redux";
import UsersDialog from "../../components/UsersDialog";
import { Pagination } from "../../models/requests/Pagination";
import { PostLikesData } from "../../models/responses/PostLikesData";
import { UserHeader } from "../../models/responses/UserHeader";
import PostLikeAPIService from "../../services/PostLikeAPIService";
import CommentLikeAPIService from "../../services/CommentLikeAPIService";
import { Page } from "../../models/responses/Page";
import { unstable_batchedUpdates } from "react-dom";

const CommentLikes = (props: {
    commentId: string,
    commentLikes: number
}) => {

    const [users, setUsers] = React.useState<UserHeader[]>([])
    const [isDialogOpen, setIsDialogOpen] = React.useState<boolean>(false)

    const handleShowLikes = () => {

        const pagination: Pagination = {
            page: 0,
            size: 12
        };
      
        CommentLikeAPIService.getPage(props.commentId, pagination)
        .then((response) => {
            const convertedResponse: Page = response.data;
            const newLikes: UserHeader[] = convertedResponse.content
        
            unstable_batchedUpdates(() => {
                setUsers(newLikes)
                setIsDialogOpen(true)
            })
        })
    }

    if(props.commentLikes == 0){
        return <></>
    }

    return (
        <>
            <button 
                className="grey-button-outlined" onClick={handleShowLikes}
            >
                {props.commentLikes} polubień
            </button>
            <UsersDialog
                users={users}
                isOpen={isDialogOpen}
                setIsOpen={setIsDialogOpen}
                handleClose={() => setIsDialogOpen(false)}
            />
        </>
    )
}

export default CommentLikes;