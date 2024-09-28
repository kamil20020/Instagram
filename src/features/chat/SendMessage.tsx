import { useState } from "react";

const SendMessage = () => {

    const [message, setMessage] = useState<string>("")

    const handleSendMessage = () => {

        setMessage("")
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