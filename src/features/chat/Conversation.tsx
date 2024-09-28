import { Client, IMessage } from "@stomp/stompjs";
import { useState, useEffect } from "react";
import { Message } from "../../models/Message";
import MessageView from "./MessageView";

const Conversation = () => {

    const [messages, setMessages] = useState<Message[]>([])

    const handleOnSubscribeNewMessagesQueue = (message: IMessage) => {

        console.log(`Received message ${message.body}`)
    
        const rawMessage = message.body
        const convertedMessage: Message = JSON.parse(rawMessage)

        setMessages((state) => ([...state, convertedMessage]))
    }
    useEffect(() => {

        const stompClient = new Client({
          brokerURL: 'ws://localhost:9300/ws',
          onConnect: () => {
    
            stompClient.subscribe(
                `/queue/3`, 
                handleOnSubscribeNewMessagesQueue,
            )
          }
        })

        window.addEventListener("beforeunload", (e) => {  
            e.preventDefault();
      
            stompClient.deactivate()
        });
      
        stompClient.activate()
    }, [])

    return (
        <div className="conversation">
            {messages.map((message: Message) => (
                <MessageView key={message.id} message={message}/>
            ))}
        </div>
    )
}

export default Conversation;