import { useLocation, useParams } from "react-router-dom";
import Avatar from "../../components/Avatar";
import UserAPIService from "../../services/UserAPIService";
import PostHeaderView from "../post/PostHeaderView";
import React from "react";
import { PostHeader } from "../../models/responses/PostHeader";
import { Page } from "../../models/responses/Page";
import PostView from "../post/post-view/PostView";
import DialogPostView from "../post/post-view/DialogPostView";
import PostAPIService from "../../services/PostAPIService";

const UserPosts = () => {

  const userId = useParams().id
  const location = useLocation()

  const [posts, setPosts] = React.useState<PostHeader[]>([])

  React.useEffect(() => {
    if(!userId){
      return;
    }

    PostAPIService.getUserPostsBasicInfoPage(userId, {page: 0, size: 12})
    .then((response) => {
      const pagedResponse: Page = response.data
      setPosts(pagedResponse.content)
    })
}, [])

  if(!userId){
    return <></>
  }

  return (
    <div className="posts">
      {posts.map((post: PostHeader) => (
        <PostHeaderView key={post.id} postHeader={post}/>
      ))}
      {location.state !== null && <DialogPostView/>}
    </div>
  );
};

export default UserPosts;
