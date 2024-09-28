import { useAuth0 } from "@auth0/auth0-react";
import Conversation from "./Conversation";
import ConversationUser from "./ConversationUser";
import SendMessage from "./SendMessage";

const RightPanel = () => {

    const {user} = useAuth0()

    const userAccountId = user?.sub

    console.log(user)

    return (
        <div id="right-panel">
            <ConversationUser/>
            <Conversation/>
            <SendMessage/>
        </div>
    )
}

export default RightPanel;