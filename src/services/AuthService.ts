class AuthService {

    public getAuthorizationBearerHeader(accessToken: string){
        
        return `Authorization: Bearer ${accessToken}`
    }
    public setAccessToken(accessToken: string){
        localStorage.setItem("access_token", accessToken)
    }
}

export default new AuthService()