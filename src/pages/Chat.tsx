import { useParams } from "react-router-dom";

const Chat = () => {

    const chatUrl = process.env.REACT_APP_CHAT_URL

    const accountId = useParams().accountId

    return (
        <iframe src={`${chatUrl}/${accountId}`} height="100%" width="100%">
        </iframe>
    )
}

export default Chat;