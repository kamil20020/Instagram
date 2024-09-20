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
  const [page, setPage] = React.useState<number>(0)
  const [totalPages, setTotalPages] = React.useState<number>(0)
  const pageSize = 12

  const [posts, setPosts] = React.useState<PostHeader[]>([])

  const onDelete = (id: string) => {
    setPosts(
      posts.filter((post) => post.id !== id)
    )
  }

  const handleGetPostsPage = (newPage: number, doReplace: boolean = false) => {

    if(!userId){
      return;
    }

    PostAPIService.getUserPostsBasicInfoPage(userId as string, {page: newPage, size: pageSize})
    .then((response) => {
      const pagedResponse: Page = response.data
      const gotPosts: PostHeader[] = pagedResponse.content

      if(doReplace){
        setPosts([...gotPosts])
      }
      else{
        setPosts([...posts, ...gotPosts])
      }

      setPage(newPage)
      setTotalPages(pagedResponse.totalPages)
    })
  }

  React.useEffect(() => {

    handleGetPostsPage(page, true)
  }, [userId])

  if(!userId){
    return <></>
  }

  return (
    <div style={{display: "flex", flexDirection: "column"}}>
      <div className="posts">
        {posts.map((post: PostHeader) => (
          <PostHeaderView key={post.id} postHeader={post}/>
        ))}
      </div>
      {page < (totalPages - 1) &&
        <div className="load-more-posts" style={{display: "flex", justifyContent: "center"}}>
          <button 
            className="grey-button"
            style={{marginTop: 22, marginBottom: 20}}
            onClick={() => handleGetPostsPage(page + 1)} 
          >
            Doładuj posty
          </button>
        </div>
      }
      {location.state !== null && <DialogPostView onDelete={onDelete}/>}
    </div>
  );
};

export default UserPosts;
