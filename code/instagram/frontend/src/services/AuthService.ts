import axios from "axios";

class AuthService {
  setRequestsAccessToken(accessToken: string) {
    axios.defaults.headers.common["Authorization"] = `Bearer ${accessToken}`;
  }

  removeRequestsAccessToken(){
    delete axios.defaults.headers["Authorization"]
  }
}

export default new AuthService();
