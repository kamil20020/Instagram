import React from "react";
import PostImage from "../../../components/PostImage";
import PostAPIService from "../../../services/PostAPIService";
import { Post } from "../../../models/responses/PostDetails";
import SimpleProfileHeader from "../../../components/SimpleProfileHeader";
import PostComments from "./PostComments";
import CreateCommentView from "../../comment/CreateCommentView";
import PostLikes from "./PostLikes";
import PostActions from "./PostActions";

const PostView = (props: {
  id: string,
  onDelete: (id: string) => void;
}) => {
  const [post, setPost] = React.useState<Post>();

  const isLikedPost = React.useRef<boolean>(false)
  const postLikes = React.useRef<number>(0)

  React.useEffect(() => {
    PostAPIService.getById(props.id)
    .then((response) => {
      const newPostData: Post = response.data

      setPost(newPostData);

      isLikedPost.current = newPostData.didLoggedUserLikePost
      postLikes.current = newPostData.likesCount
    });
  }, []);

  if (!post) {
    return <div></div>;
  }

  return (
    <div className="post-view">
      <PostImage img={`data:image/png;base64,${post.content}`} />
      <div className="post-details">
        <div className="post-author-info">
          <SimpleProfileHeader
            avatar={post.author.avatar}
            nickname={post.author.nickname}
            userId={post.author.id}
          />
          <PostActions 
            post={post}
            onDelete={props.onDelete}
          />
        </div>
        <PostComments post={post}/>
        <PostLikes 
          post={post} 
          postLikes={postLikes.current} 
          isLikedPost={isLikedPost.current}
        />
        {!post.areDisabledComments && <CreateCommentView postId={props.id} />}
      </div>
    </div>
  );
};

export default PostView;
