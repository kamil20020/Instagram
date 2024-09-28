import { useParams } from "react-router";

const ConversationUser = () => {

    const params = useParams()

    const userAccountId = params.userId

    return (
        <div className="conversation-user">
            Użytkownik {userAccountId}
        </div>
    )
}

export default ConversationUser;