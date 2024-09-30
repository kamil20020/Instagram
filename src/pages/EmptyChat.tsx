const EmptyChat = () => {

    const chatUrl = process.env.REACT_APP_CHAT_URL

    return (
        <iframe src={chatUrl} height="100%" width="100%" sandbox="allow-popups allow-scripts allow-same-origin allow-forms">
        </iframe>
    )
}

export default EmptyChat;