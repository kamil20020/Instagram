import { Message, Client } from "@stomp/stompjs"
import { useState, useEffect } from "react"
import Contacts from "../features/chat/Contacts"
import Conversation from "../features/chat/Conversation"
import LeftPanel from "../features/chat/LeftPanel"
import RightPanel from "../features/chat/RightPanel"

const Chat = () => {

    const [messages, setMessages] = useState<Message[]>([])

    useEffect(() => {

        const stompClient = new Client({
          brokerURL: 'ws://localhost:9300/ws',
          onConnect: () => {
    
            stompClient.subscribe(
              `/queue/3`, 
              message => {
                console.log(`Received message ${message.body}`)
    
                const rawMessage = message.body
                const convertedMessage: Message = JSON.parse(rawMessage)
    
                setMessages((state) => [...state, convertedMessage])
              },
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
        <div id="chat">
          <LeftPanel/>
          <RightPanel/>
        </div>
    )
}

export default Chat;