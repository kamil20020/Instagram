import axios from "axios";
import { Pagination } from "../models/requests/Pagination";
import { CreateComment } from "../models/requests/CreateComment";

class CommentAPIService {

    private api: string = `${process.env.REACT_APP_API}/posts`;
    
    getCommentsPage(postId: string, pagination: Pagination, parentCommentId?: string){
        return axios.get(`${this.api}/${postId}/comments`, {
            params: {
                parentCommentId,
                ...pagination
            }
        })
    }

    createComment(postId: string, request: CreateComment, parentCommentId?: string){
        return axios.post(`${this.api}/${postId}/comments/${parentCommentId}`, request)
    }

    deleteComment(commentId: string){
        return axios.delete(`${this.api}/comments/${commentId}`)
    }
}

export default new CommentAPIService()