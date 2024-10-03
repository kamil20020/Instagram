import { useAuth0 } from "@auth0/auth0-react";
import Conversation from "./Conversation";
import ConversationUser from "./ConversationUser";
import SendMessage from "./SendMessage";

const RightPanel = () => {

    return (
        <div id="right-panel">
            <ConversationUser/>
            <Conversation/>
        </div>
    )
}

export default RightPanel;