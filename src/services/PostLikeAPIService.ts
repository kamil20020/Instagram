import axios from "axios"
import { Pagination } from "../models/requests/Pagination";

class PostLikeAPIService {

    private api = `${process.env.REACT_APP_API}/posts`

    getPage(postId: string, pagination: Pagination){
        return axios.get(`${this.api}/${postId}/likes`, {
            params: {
                ...pagination
            }
        })
    }

    create(postId: string){
        return axios.post(`${this.api}/${postId}/likes`)
    }

    delete(postId: string){
        return axios.delete(`${this.api}/${postId}/likes`)
    }
}

export default new PostLikeAPIService();