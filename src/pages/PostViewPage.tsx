import React from "react";
import { useNavigate, useParams } from "react-router-dom";
import PostView from "../features/post/post-view/PostView";
import { useSelector } from "react-redux";
import { useAuthSelector } from "../redux/slices/authSlice";

const PostViewPage = () => {
  const postId = useParams().id;

  const loggedUserId = useSelector(useAuthSelector).user?.id

  const navigate = useNavigate()

  if (!postId) {
    return <div></div>;
  }

  const onDelete = (id: string) => {
    navigate(`/profile/${loggedUserId}`)
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
        <PostView id={postId} onDelete={onDelete}/>
      </div>
    </div>
  );
};

export default PostViewPage;
