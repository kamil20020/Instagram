﻿import axios from "axios";
import { CreatePost } from "../models/requests/CreatePost";
import { Pagination } from "../models/requests/Pagination";

class PostAPIService {
  private api: string = `${process.env.REACT_APP_API}/posts`;

  getById(id: string) {
    return axios.get(`${this.api}/${id}`)
  }

  getUserPostsBasicInfoPage(userId: string, pagination: Pagination){
    return axios.get(`${this.api}/author/${userId}`, {
        params: {...pagination},
    })
  }

  createPost(request: CreatePost) {
    return axios.post(`${this.api}`, request);
  }

  deletePost(postId: string) {
    return axios.delete(`${this.api}/${postId}`);
  }
}

export default new PostAPIService();
