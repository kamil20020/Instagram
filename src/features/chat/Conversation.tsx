import Message from "./Message";

export interface SimpleMessage {
    id: number,
    senderId: string,
    content: string,
    creationDate: Date
}

const messages: SimpleMessage[] = [
    {
        id: 1,
        senderId: "1",
        content: "Siema",
        creationDate: new Date()
    },
    {
        id: 2,
        senderId: "2",
        content: "Elo",
        creationDate: new Date()
    },
    {
        id: 3,
        senderId: "1",
        content: "Jak było",
        creationDate: new Date()
    },
    {
        id: 4,
        senderId: "1",
        content: "Wczoraj?",
        creationDate: new Date()
    },
    {
        id: 5,
        senderId: "2",
        content: "W porządku",
        creationDate: new Date()
    },
    {
        id: 7,
        senderId: "2",
        content: "Nudy",
        creationDate: new Date()
    },
]

const Conversation = () => {

    return (
        <div className="conversation">
            {messages.map((message: SimpleMessage) => (
                <Message key={message.id} message={message}/>
            ))}
        </div>
    )
}

export default Conversation;