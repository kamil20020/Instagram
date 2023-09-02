import React from "react";
import { useParams } from "react-router-dom";
import PostView from "../features/post/post-view/PostView";

const PostViewPage = () => {

  const postId = useParams().id

  if(!postId){
    return <div></div>
  }

  return (
    <PostView id={postId}/>
  );
};

export default PostViewPage;
