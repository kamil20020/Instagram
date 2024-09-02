import { useAuth0 } from "@auth0/auth0-react";
import React, { useEffect } from "react";
import FollowerAPIService from "../../../services/FollowerAPIService";

const FollowUser = (props: {
    userId: string,
    doesFollow: boolean
}) => {

    const [doFollowed, setDoFollowed] = React.useState<boolean>(props.doesFollow)

    const {isAuthenticated} = useAuth0()

    useEffect(() => {

        setDoFollowed(props.doesFollow)
    }, [props.userId])

    const handleCreateFollow = () => {

        if(!isAuthenticated){
            return;
        }

        FollowerAPIService.create(props.userId)
        .then(() => {
            setDoFollowed(true)
        })
    }

    const handleDeleteFollow = () => {

        FollowerAPIService.delete(props.userId)
        .then(() => {
            setDoFollowed(false)
        })
    }

    return (
        <>
            {doFollowed ? 
                    <button className="grey-button" onClick={handleDeleteFollow}>Zaobserwowano</button>
                :
                    <button className="blue-button-filled" onClick={handleCreateFollow}>Obserwuj</button>
            }
        </>
    )
}

export default FollowUser;