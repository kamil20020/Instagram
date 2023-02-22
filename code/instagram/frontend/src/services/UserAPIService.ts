import axios from "axios"
import qs from "qs"

class UserAPIService {

    api: string = "http://localhost:9000/user"

    getUserById(userId: number){
        return axios.get(`${this.api}/${userId}`)
    }

    searchUser(input: string){
        return axios.get(`${this.api}/search?`, {
            params: {
                input: input
            }
        })
    }

    getUsersByIds(ids: number[]){
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
        return fetch(`${this.api}/search/?${new URLSearchParams({input: input})}`)
    }
}

export default new UserAPIService()