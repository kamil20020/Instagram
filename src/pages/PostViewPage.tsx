import React from "react";
import { useParams } from "react-router-dom";
import PostView from "../features/post/post-view/PostView";

const PostViewPage = () => {
  const postId = useParams().id;

  if (!postId) {
    return <div></div>;
  }

  return (
    <div
      style={{
        display: "flex",
        justifyContent: "center",
        alignItems: "center",
        paddingTop: 20,
        paddingBottom: 20,
        height: "calc(100% - 20px)"
      }}
    >
      <div style={{ border: "1px solid silver", width: "100%", height: "100%",}}>
        <PostView id={postId} />
      </div>
    </div>
  );
};

export default PostViewPage;
