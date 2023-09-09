import axios from "axios"
import qs from "qs"
import { Pagination } from "../models/Pagination"

class UserAPIService {

    private api: string = "http://localhost:9000/users"

    getBasicUserById(userId: string){
        return axios.get(`${this.api}/${userId}/basic`)
    }

    getUserProfileById(userId: string){
        return axios.get(`${this.api}/${userId}/profile`)
    }

    getBasicUserByUserAccountId(userAccountId: string){
        return axios.get(`${this.api}/userAccount/${userAccountId}`)
    }

    searchUser(input: string){
        return axios.get(`${this.api}`, {
            params: {
                phrase: input
            }
        })
    }

    getUsersByIds(ids: string[]){
        return axios.get(`${this.api}/ids`, {
            params: {
                ids: ids
            },
            paramsSerializer: {
                indexes: null
            }
        })
    }

    getUserPostsHeadersPage(userId: string, pagination?: Pagination){
        return axios.get(`${this.api}/${userId}/posts`, {
            params: {...pagination},
        })
    }

    searchUserFetchAPi(input: string){
        return fetch(`${this.api}/?${new URLSearchParams({phrase: input})}`)
    }

    createUser(userAccountId: string){
        return axios.post(`${this.api}/userAccount/${userAccountId}`)
    }
}

export default new UserAPIService()