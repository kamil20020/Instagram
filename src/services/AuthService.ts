class AuthService {

    public getAuthorizationBearerHeader(accessToken: string){
        
        return `Authorization: Bearer ${accessToken}`
    }
}

export default new AuthService()