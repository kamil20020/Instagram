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

    const [page, setPage] = React.useState<number>(0);
    const [pagesCount, setPagesCount] = React.useState<number>(0);
    const pageSize = 12;

    const handleShowLikes = (doReplace: boolean = false) => {

        const newPage = doReplace ? 0 : page + 1

        const pagination: Pagination = {
            page: newPage,
            size: pageSize
        };
      
        CommentLikeAPIService.getPage(props.commentId, pagination)
        .then((response) => {
            const convertedResponse: Page = response.data;
            const newLikes: UserHeader[] = convertedResponse.content
        
            unstable_batchedUpdates(() => {
                setIsDialogOpen(true)

                if(doReplace){
                    setUsers(newLikes)
                }
                else{
                    setUsers([...users, ...newLikes])
                }

                setPage(newPage)
                setPagesCount(convertedResponse.totalPages)
            })
        })
    }

    if(props.commentLikes == 0){
        return <></>
    }

    return (
        <>
            <button 
                className="grey-button-outlined" onClick={() => handleShowLikes(true)}
            >
                {props.commentLikes} polubień
            </button>
            <UsersDialog
                users={users}
                isOpen={isDialogOpen}
                page={page}
                pagesCount={pagesCount}
                loadUsers={() => handleShowLikes(false)}
                setIsOpen={setIsDialogOpen}
                handleClose={() => setIsDialogOpen(false)}
            />
        </>
    )
}

export default CommentLikes;