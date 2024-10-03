import { useState } from "react";
import SavedMessageService, { CreateMessage } from "../../services/SavedMessageService";
import { useParams } from "react-router";
import { Message } from "../../models/Message";

const SendMessage = (props: {
    handleAppendMessage: (message: Message) => void
}) => {

    const otherUserAccountId = useParams().accountId as string

    const [message, setMessage] = useState<string>("")

    const handleSendMessage = () => {

        const request: CreateMessage = {
            receiverAccountId: otherUserAccountId,
            content: message
        }

        SavedMessageService.createMessage(request)
        .then((response) => {

            const gotMessage: Message = response.data

            console.log(gotMessage)

            props.handleAppendMessage(gotMessage)
            
            setMessage("")
        })
    }

    return (
        <div className="send-message">
            <input 
                type="text" 
                placeholder="Wyślij wiadomość" 
                value={message} 
                onChange={(event) => {
                    setMessage(event.target.value)
                }}
            />
            {message &&
                <button onClick={handleSendMessage}>
                    Wyślij
                </button>
            }
        </div>
    )
}

export default SendMessage;