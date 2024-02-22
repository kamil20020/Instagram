import axios from "axios";
import { Pagination } from "../models/Pagination";

export interface CreateComment {
    userId: string,
    content: string,
    parentCommentId?: string
}


class CommentAPIService {

    private api: string = `${process.env.REACT_APP_API}/posts`;
    
    getCommentsPage(postId: string, pagination: Pagination){
        return axios.get(`${this.api}/${postId}/comments`, {
            params: {
                page: pagination.page,
                size: pagination.size
            }
        })
    }

    createComment(postId: string, request: CreateComment){
        return axios.post(`${this.api}/${postId}/comments`, request)
    }
}

export default new CommentAPIService()