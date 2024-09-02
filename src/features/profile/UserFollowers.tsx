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

    const handleLoadFollowers = () => {

        if(props.followersCount == 0){
            return;
        }

        const pagination: Pagination = {
            page: 0,
            size: 5
        };

        FollowerAPIService.getFollowersPage(props.userId, pagination)
        .then((response) => {
            const pagedResponse: Page = response.data
            
            unstable_batchedUpdates(() => {
                setIsOpenDialog(true)
                setFollowers(pagedResponse.content)
            })
        })
    }

    return (
        <>
            <div className="user-stat opacity-hover" onClick={handleLoadFollowers}>
                <h3>{props.followersCount}&nbsp;</h3>
                <h3 className="normal-weight">obserwujących</h3>
            </div>
            <UsersDialog
                users={followers}
                isOpen={isOpenDialog}
                setIsOpen={setIsOpenDialog}
                handleClose={() => setIsOpenDialog(false)}
            />
        </>
    );
}

export default UserFollowers;