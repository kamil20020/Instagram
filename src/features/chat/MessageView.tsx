import { useAuth0 } from "@auth0/auth0-react"
import { Message } from "../../models/Message"

const MessageView = (props: {
    message: Message
}) => {

    const {user} = useAuth0()

    const userAccountId = user?.sub

    console.log(user)

    const message = props.message

    const getMessagePositionStyle = (): string => {

        if(message.senderAccountId === userAccountId){
            return "flex-end";
        }

        return "flex-start";
    }

    const getMessageBackgroudColor = (): string => {

        if(message.senderAccountId === userAccountId){
            return "#e3e3e3";
        }

        return "white";
    }

    return (
        <div className="message">
            <h5 className="message-date">{message.creationDate.toLocaleString()}</h5>
            <div 
                className="message-content" 
                style={{
                    alignSelf: getMessagePositionStyle(),
                    backgroundColor: getMessageBackgroudColor()
                }}
            >
                {message.content}
            </div>
        </div>
    )
}

export default MessageView;