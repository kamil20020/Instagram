class AuthService {

    public getAuthorizationBearerHeader(accessToken: string){
        
        return `Bearer ${accessToken}`
    }

    public getAccessToken(){
        return localStorage.getItem("access_token")
    }

    public setAccessToken(accessToken: string){
        localStorage.setItem("access_token", accessToken)
    }

    public logout(){
        localStorage.clear()
    }
}

export default new AuthService()