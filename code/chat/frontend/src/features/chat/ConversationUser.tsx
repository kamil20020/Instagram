import { useContext, useEffect, useState } from "react";
import { useParams } from "react-router";
import { UserContext, UserData } from "../../context/UserContext";
import UserService from "../../services/UserService";
import { User } from "../../models/User";
import Loading from "../../info/Loading";
import UserHeader from "../../components/UserHeader";

const ConversationUser = (props: {
    user?: UserData
}) => {

    const userAccountId = useParams().accountId as string

    const [user, setUser] = useState<UserData | undefined>(props.user)

    useEffect(() => {

        if(props.user){
            return;
        }

        UserService.getUserHeaderByAccountId(userAccountId)
        .then((response) => {
            console.log(response.data)
            setUser(response.data)
        })
    }, [userAccountId])

    if(!user){
        return <Loading/>
    }

    return (
        <div className="conversation-user">
            <UserHeader nickname={user.nickname} avatar={user.avatar}/>
        </div>
    )
}

export default ConversationUser;