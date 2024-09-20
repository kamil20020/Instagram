import axios from "axios";
import { Pagination } from "../models/requests/Pagination";

class FollowerAPIService {

    private api = `${process.env.REACT_APP_API}/users`

    getFollowersPage(userId: string, pagination: Pagination){
        return axios.get(`${this.api}/${userId}/followers`, {
            params: {
                ...pagination
            }
        })
    }

    getFollowedPage(userId: string, pagination: Pagination){
        return axios.get(`${this.api}/${userId}/followed`, {
            params: {
                ...pagination
            }
        })
    }

    getPostsPage(pagination: Pagination){
        return axios.get(`${this.api}/followed/posts`, {
            params: {
                ...pagination
            }
        })
    }

    create(userId: string){
        return axios.post(`${this.api}/${userId}/followers`)
    }

    delete(userId: string){
        return axios.delete(`${this.api}/${userId}/followers`)
    }
}

export default new FollowerAPIService();