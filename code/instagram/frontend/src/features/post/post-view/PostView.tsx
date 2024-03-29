﻿import React from "react";
import PostImage from "../../../components/PostImage";
import PostAPIService from "../../../services/PostAPIService";
import { Post } from "../../../models/Post";
import SimpleProfileHeader from "../../../components/SimpleProfileHeader";
import PostComments from "./PostComments";
import IconWithText from "../../../components/IconWithText";
import Icon from "../../../components/Icon";
import CreateCommentView from "../../comment/CreateCommentView";

const PostView = (props: { id: string }) => {
  const [post, setPost] = React.useState<Post>();

  React.useEffect(() => {
    PostAPIService.getById(props.id)
    .then((response) => {
      setPost(response.data);
      console.log(response.data);
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
        <PostComments post={post}/>
        <div className="post-info">
          <div className="post-info-actions">
            <button className="outlined-button">
              <Icon iconName="favorite" />
            </button>
            <button className="outlined-button">
              <Icon iconName="mode_comment" />
            </button>
          </div>
          <div>
            <h4>Liczba polubień: 77 197</h4>
            <span style={{ color: "rgb(115, 115, 115)", marginTop: 2 }}>
              {new Date(post.creationDatetime).toLocaleDateString()}
            </span>
          </div>
        </div>
        <CreateCommentView postId={props.id} />
      </div>
    </div>
  );
};

export default PostView;
