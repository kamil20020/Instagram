import { useLocation, useParams } from "react-router-dom";
import Avatar from "../../components/Avatar";
import UserAPIService from "../../services/UserAPIService";
import PostHeaderView from "../post/PostHeaderView";
import React from "react";
import { PostHeader } from "../../models/PostHeader";
import { Page } from "../../models/Page";
import PostView from "../post/post-view/PostView";
import DialogPostView from "../post/post-view/DialogPostView";

const img1 =
  "https://www.imperiumtapet.com/public/uploads/preview/piekne-widoki-7-3315352142308iyuwjrhvf.jpg";
const img2 =
  "https://img.freepik.com/darmowe-zdjecie/gory-vestrahorn-w-stokksnes-na-islandii_335224-667.jpg?w=1380&t=st=1674737578~exp=1674738178~hmac=c701828364cf1333d4ab301db868dabee2a0231cb886bd09dc709852524ae1d9";
const img3 =
  "https://www.galerie-zdjec.pl/wp-content/uploads/2011/02/piekne-widoki-5.jpg";
const img4 = "https://s29.flog.pl/media/foto/13671790_kto-ma-zawsze-pod-gorke-ten-pozniejma-ladne-widoki--.jpg"


const UserPosts = () => {

  const userId = useParams().id
  const location = useLocation()

  const [posts, setPosts] = React.useState<PostHeader[]>([])

  React.useEffect(() => {
    if(!userId){
      return;
    }

    UserAPIService.getUserPostsHeadersPage(userId, {page: 0, size: 200})
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
