import axios from "axios";
import { CreatePost } from "../models/requests/CreatePost";
import { Pagination } from "../models/requests/Pagination";

class PostAPIService {
  private api: string = `${process.env.REACT_APP_API}/posts`;

  getById(id: string) {
    return axios.get(`${this.api}/${id}`)
  }

  getUserPostsBasicInfoPage(userId: string, pagination: Pagination){
    return axios.get(`${this.api}/${userId}/posts`, {
        params: {...pagination},
    })
  }

  createPost(request: CreatePost) {
    return axios.post(`${this.api}`, request);
  }

  deletePost(postId: string) {
    return axios.delete(`${this.api}/${postId}`);
  }

  fixBase64(base64: string){
    return base64.split(',')[1]
  }
}

export default new PostAPIService();
