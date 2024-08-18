import axios from "axios"
import qs from "qs"
import { Pagination } from "../models/requests/Pagination"
import { PatchUser } from "../models/requests/PatchUser";
import { PersonalData } from "../models/requests/PersonalData";

class UserAPIService {

    private api: string = `${process.env.REACT_APP_API}/users`;

    getUserBasicInfoById(userId: string){
        return axios.get(`${this.api}/${userId}/header`)
    }

    getUserProfileById(userId: string){
        return axios.get(`${this.api}/${userId}/profile`)
    }

    getUserBasicInfoByUserAccountId(userAccountId: string){
        return axios.get(`${this.api}/user-account/${userAccountId}/header`)
    }

    searchUser(phrase: string, pagination: Pagination){
        return axios.get(`${this.api}`, {
            params: {
                phrase: phrase,
                ...pagination
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

    registerUser(userAccountId: string){
        return axios.post(`${this.api}/user-account`, {
            params: {
                accountId: userAccountId
            }
        })
    }

    fillPersonalData(personalData: PersonalData){
        return axios.post(`${this.api}/fill-personal-data`, personalData)
    }

    patchLoggedUser(patchUser: PatchUser){
        return axios.patch(`${this.api}`, patchUser)
    }
}

export default new UserAPIService()