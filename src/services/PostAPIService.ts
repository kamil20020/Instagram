import axios from "axios";

export interface CreatePost {
  description?: string;
  img: string;
  areHiddenLikes: boolean;
  areDisabledComments: boolean;
  userId: string;
}

class PostAPIService {
  private api: string = "http://localhost:9000/posts";

  getById(id: string) {
    return axios.get(`${this.api}/${id}`)
  }

  createPost(request: CreatePost) {
    return axios.post(`${this.api}`, request);
  }

  fixBase64(base64: string){
    return base64.split(',')[1]
  }
}

export default new PostAPIService();
