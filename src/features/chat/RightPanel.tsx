import Conversation from "./Conversation";
import ConversationUser from "./ConversationUser";
import SendMessage from "./SendMessage";

const RightPanel = () => {

    return (
        <div id="right-panel">
            <ConversationUser/>
            <Conversation/>
            <SendMessage/>
        </div>
    )
}

export default RightPanel;