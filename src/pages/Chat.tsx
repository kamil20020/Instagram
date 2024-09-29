import { useParams } from "react-router-dom";

const Chat = () => {

    const chatUrl = process.env.REACT_APP_CHAT_URL

    const userId = useParams().userId

    return (
        <iframe src={`${chatUrl}/${userId}`} height="100%" width="100%">
        </iframe>
    )
}

export default Chat;