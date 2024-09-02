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

    const handleLoadFollowed = () => {

        if(props.followedCount == 0){
            return;
        }

        const pagination: Pagination = {
            page: 0,
            size: 5
        };

        FollowerAPIService.getFollowedPage(props.userId, pagination)
        .then((response) => {
            const pagedResponse: Page = response.data

            unstable_batchedUpdates(() => {
                setIsOpenDialog(true)
                setFollowed(pagedResponse.content)
            })
        })
    }

    return (
        <>
            <div className="user-stat opacity-hover" onClick={handleLoadFollowed}>
                <h3 className="normal-weight">Obserwowani:&nbsp;</h3>
                <h3>{props.followedCount}</h3>
            </div>
            <UsersDialog
                users={followed}
                isOpen={isOpenDialog}
                setIsOpen={setIsOpenDialog}
                handleClose={() => setIsOpenDialog(false)}
            />
        </>
    );
}

export default UserFollowed;