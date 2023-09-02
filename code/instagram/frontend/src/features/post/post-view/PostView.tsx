import React from "react";
import PostImage from "../../../components/PostImage";
import PostAPIService from "../../../services/PostAPIService";
import { Post } from "../../../models/Post";
import SimpleProfileHeader from "../../../components/SimpleProfileHeader";
import PostComments from "./PostComments";
import IconWithText from "../../../components/IconWithText";

const PostView = (props: { id: string }) => {
  const [post, setPost] = React.useState<Post>();

  React.useEffect(() => {
    PostAPIService.getById(props.id).then((response) => {
      setPost(response.data);
    });
  }, []);

  if (!post) {
    return <div></div>;
  }

  return (
    <div className="post-view">
      <PostImage img={`data:image/png;base64,${post.img}`} />
      <div className="post-details">
        <div className="post-author-info">
          <SimpleProfileHeader
            avatar={post.userData.avatar}
            nickname={post.userData.nickname}
          />
          <button className="outlined-button">
            <IconWithText iconName="more_horiz" />
          </button>
        </div>
        <PostComments />
        <div className="post-info">AA</div>
      </div>
    </div>
  );
};

export default PostView;
