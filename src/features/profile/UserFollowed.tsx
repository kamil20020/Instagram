import React from "react";
import { UserHeader } from "../../models/responses/UserHeader";
import FollowerAPIService from "../../services/FollowerAPIService";
import { Pagination } from "../../models/requests/Pagination";
import { Page } from "../../models/responses/Page";
import { unstable_batchedUpdates } from "react-dom";
import UsersDialog from "../../components/UsersDialog";

const UserFollowed = (props: {
    userId: string,
    followedCount: number
}) => {

    const [followed, setFollowed] = React.useState<UserHeader[]>([])
    const [isOpenDialog, setIsOpenDialog] = React.useState<boolean>(false)

    const [page, setPage] = React.useState<number>(0)
    const [pagesCount, setPagesCount] = React.useState<number>(0)
    const pageSize = 12;

    const handleLoadFollowedUsersPage = (doReplace: boolean = false) => {

        if(props.followedCount == 0){
            return;
        }

        const newPage = doReplace ? 0 : page + 1

        const pagination: Pagination = {
            page: newPage,
            size: pageSize
        };

        FollowerAPIService.getFollowedPage(props.userId, pagination)
        .then((response) => {
            const pagedResponse: Page = response.data

            const newFollowedUsers: UserHeader[] = pagedResponse.content

            unstable_batchedUpdates(() => {
                setIsOpenDialog(true)

                if(doReplace){
                    setFollowed(newFollowedUsers)
                }
                else{
                    setFollowed([...followed, ...newFollowedUsers])
                }

                setPage(newPage)
                setPagesCount(pagedResponse.totalPages)
            })
        })
    }

    return (
        <>
            <div className="user-stat opacity-hover" onClick={() => handleLoadFollowedUsersPage(true)}>
                <h3 className="normal-weight">Obserwowani:&nbsp;</h3>
                <h3>{props.followedCount}</h3>
            </div>
            <UsersDialog
                users={followed}
                isOpen={isOpenDialog}
                page={page}
                pagesCount={pagesCount}
                setIsOpen={setIsOpenDialog}
                loadUsers={() => handleLoadFollowedUsersPage(false)}
                handleClose={() => setIsOpenDialog(false)}
            />
        </>
    );
}

export default UserFollowed;