import { Client, IMessage } from "@stomp/stompjs";
import { useState, useEffect, useContext, useRef } from "react";
import { Message } from "../../models/Message";
import MessageView from "./MessageView";
import SavedMessageService from "../../services/SavedMessageService";
import { useParams } from "react-router";
import { Page } from "../../models/Page";
import SendMessage from "./SendMessage";
import { useAuth0 } from "@auth0/auth0-react";
import { UserContext } from "../../context/UserContext";

const Conversation = () => {

    const receiverApiUrl = process.env.REACT_APP_RECEIVER_API_URL

    const otherUserAccountId = useParams().accountId as string
    
    const accountId = useContext(UserContext)?.user?.accountId

    const [messages, setMessages] = useState<Message[]>([])

    const messagesEndRef = useRef(null)

    const handleSubscribeNewMessagesFromQueue = (message: IMessage) => {

        console.log(`Received message ${message.body}`)
    
        const rawMessage = message.body
        const convertedMessage: Message = JSON.parse(rawMessage)

        setMessages((state) => ([...state, convertedMessage]))
    }

    const loadMessages = () => {

        SavedMessageService.getConversationMessages(otherUserAccountId)
        .then((response) => {
            const gotPage: Page<Message> = response.data

            setMessages(gotPage.content)
        })
        .catch((error) => {
            console.log(error)
        })
    }

    const listenNewMessages = () => {

        const stompClient = new Client({
            brokerURL: receiverApiUrl,
            onConnect: () => {
        
                stompClient.subscribe(
                    `/queue/${accountId}`, 
                    handleSubscribeNewMessagesFromQueue,
                )
            }
        })
  
        window.addEventListener("beforeunload", (e) => {  
            e.preventDefault();
    
            stompClient.deactivate()
        });
        
        stompClient.activate()
    }

    const appendMessage = (newMessage: Message) => {

        setMessages((state) => ([...state, newMessage]))
    }

    useEffect(() => {

        loadMessages()
        listenNewMessages()
    }, [])

    return (
        <>
            <div className="conversation" ref={messagesEndRef}>
                {messages.map((message: Message) => (
                    <MessageView key={message.id} message={message}/>
                ))}
            </div>
            <SendMessage handleAppendMessage={appendMessage}/>
        </>
    )
}

export default Conversation;