import { useParams } from "react-router-dom";

const Chat = () => {

    const chatUrl = process.env.REACT_APP_CHAT_URL

    const accountId = useParams().accountId

    return (
        <iframe 
            src={`${window.location.origin}/${chatUrl}/${accountId}`} 
            height="100%" 
            width="100%" 
            sandbox="allow-popups allow-scripts allow-same-origin allow-forms"
        >
        </iframe>
    )
}

export default Chat;