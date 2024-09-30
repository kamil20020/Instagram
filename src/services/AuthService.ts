class AuthService {

    public getAuthorizationBearerHeader(accessToken: string){
        
        return `Bearer ${accessToken}`
    }

    public getMessagesAccessToken(){
        return localStorage.getItem("messages_access_token")
    }

    public getUsersAccessToken(){
        return localStorage.getItem("users_access_token")
    }

    public setMessagesAccessToken(accessToken: string){
        localStorage.setItem("messages_access_token", accessToken)
    }

    public setUsersAccessToken(accessToken: string){
        localStorage.setItem("users_access_token", accessToken)
    }

    public logout(){
        localStorage.clear()
    }
}

export default new AuthService()