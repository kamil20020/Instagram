import axios from "axios";

class AuthService {

  getAuthorizationHeader(access_token: string){
    return `Bearer ${access_token}`
  }

  setRequestsAccessToken(accessToken: string) {
    localStorage.setItem("access_token", accessToken)
  }

  removeRequestsAccessToken(){
    localStorage.removeItem("access_token")
    delete axios.defaults.headers["Authorization"]
  }
}

export default new AuthService();
