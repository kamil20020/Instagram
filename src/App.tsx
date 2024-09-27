import React, { useEffect, useRef, useState } from 'react';
import logo from './logo.svg';
import './App.css';
import { Client } from '@stomp/stompjs';
import { Message } from './models/Message';

function App() {

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
    <div>
      {messages.map((message: Message) => (
        <div key={message.id}>{message.content}</div>
      ))}
    </div>
  );
}

export default App;
