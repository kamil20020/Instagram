import axios from "axios"
import { Pagination } from "../models/requests/Pagination"

class CommentLikeAPIService {

    private api = `${process.env.REACT_APP_API}/comments`

    getPage(commentId: string, pagination: Pagination){
        return axios.get(`${this.api}/${commentId}/likes`, {
            params: {
                ...pagination
            }
        })
    }

    create(commentId: string){
        return axios.post(`${this.api}/${commentId}/likes`)
    }

    delete(commentId: string){
        return axios.delete(`${this.api}/${commentId}/likes`)
    }
}

export default new CommentLikeAPIService()