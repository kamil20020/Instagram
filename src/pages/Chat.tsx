import { Message, Client } from "@stomp/stompjs"
import { useState, useEffect } from "react"
import Contacts from "../features/chat/Contacts"
import Conversation from "../features/chat/Conversation"
import LeftPanel from "../features/chat/LeftPanel"
import RightPanel from "../features/chat/RightPanel"

const Chat = () => {

    return (
        <div id="chat">
          <LeftPanel/>
          <RightPanel/>
        </div>
    )
}

export default Chat;