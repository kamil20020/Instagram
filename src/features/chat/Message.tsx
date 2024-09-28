import { SimpleMessage } from "./Conversation";

const userId: string = "1"

const Message = (props: {
    message: SimpleMessage
}) => {

    const message = props.message

    return (
        <div className="message">
            <h5 style={{color: "silver"}}>{message.creationDate.toLocaleString()}</h5>
            {message.content}
        </div>
    )
}

export default Message;