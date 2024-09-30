import axios from "axios"
import { Pagination } from "../models/Pagination"

export interface CreateMessage {
    receiverAccountId: string,
    content: string
}

class SavedMessageService{

    private apiUrl = process.env.REACT_APP_SAVED_MESSAGE_API_URL + "/messages"

    getLatestUsers(pagination: Pagination){
        return axios.get(`${this.apiUrl}/latest-users`, {
            params: {
                ...pagination
            }
        })
    }

    getConversationMessages(otherUserAccountId: string){
        return axios.get(`${this.apiUrl}/${otherUserAccountId}`)
    }

    createMessage(createMessage: CreateMessage){
        return axios.post(this.apiUrl, createMessage)
    }
}

export default new SavedMessageService()