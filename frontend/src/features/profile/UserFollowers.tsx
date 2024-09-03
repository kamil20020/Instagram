import React from "react";
import FollowerAPIService from "../../services/FollowerAPIService";
import { Pagination } from "../../models/requests/Pagination";
import { UserHeader } from "../../models/responses/UserHeader";
import { Page } from "../../models/responses/Page";
import UsersDialog from "../../components/UsersDialog";
import { unstable_batchedUpdates } from "react-dom";

const UserFollowers = (props: {
    userId: string,
    followersCount: number
}) => {
    const [followers, setFollowers] = React.useState<UserHeader[]>([])
    const [isOpenDialog, setIsOpenDialog] = React.useState<boolean>(false)

    const [page, setPage] = React.useState<number>(-1);
    const [pagesCount, setPagesCount] = React.useState<number>(0);
    const pageSize = 12;

    const handleLoadFollowers = (doReplace: boolean = false) => {

        if(props.followersCount == 0){
            return;
        }

        const newPage = doReplace ? 0 : page + 1

        const pagination: Pagination = {
            page: newPage,
            size: pageSize
        };

        FollowerAPIService.getFollowersPage(props.userId, pagination)
        .then((response) => {
            const pagedResponse: Page = response.data
            
            const newFollowers: UserHeader[] = pagedResponse.content

            unstable_batchedUpdates(() => {
                setIsOpenDialog(true)

                if(doReplace){
                    setFollowers(newFollowers)
                }
                else{
                    setFollowers([...followers, ...newFollowers])
                }
                
                setPage(newPage)
                setPagesCount(pagedResponse.totalPages)
            })
        })
    }

    return (
        <>
            <div className="user-stat opacity-hover" onClick={() => handleLoadFollowers(true)}>
                <h3>{props.followersCount}&nbsp;</h3>
                <h3 className="normal-weight">obserwujących</h3>
            </div>
            <UsersDialog
                users={followers}
                isOpen={isOpenDialog}
                page={page}
                pagesCount={pagesCount}
                loadUsers={() => handleLoadFollowers(false)}
                setIsOpen={setIsOpenDialog}
                handleClose={() => setIsOpenDialog(false)}
            />
        </>
    );
}

export default UserFollowers;