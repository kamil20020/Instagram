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
        padding: 20
      }}
    >
      <div style={{ width: "100%", border: "1px solid silver" }}>
        <PostView id={postId} />
      </div>
    </div>
  );
};

export default PostViewPage;
