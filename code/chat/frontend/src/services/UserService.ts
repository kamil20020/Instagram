import axios from "axios"

class UserService{

    private apiUrl = process.env.REACT_APP_USER_API_URL + "/users"

    getUserHeaderByAccountId(accountId: string){
        return axios.get(`${this.apiUrl}/user-account/${accountId}/header`)
    }

    getUsersHeadersByAccountsIds(accountIds: string[]){
        return axios.get(`${this.apiUrl}/accountsIds`, {
            params: {
                accountIds: accountIds
            },
            paramsSerializer: {
                indexes: null
            }
        })
    }
}

export default new UserService()