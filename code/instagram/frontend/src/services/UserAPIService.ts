import axios from "axios"
import qs from "qs"

class UserAPIService {

    api: string = "http://localhost:9000/users"

    getUserById(userId: string){
        return axios.get(`${this.api}/${userId}`)
    }

    getUseProfileById(userId: string){
        return axios.get(`${this.api}/${userId}/profile`)
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

    searchUserFetchAPi(input: string){
        return fetch(`${this.api}/?${new URLSearchParams({phrase: input})}`)
    }
}

export default new UserAPIService()