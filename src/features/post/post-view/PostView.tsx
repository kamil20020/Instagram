import React from "react";
import PostImage from "../../../components/PostImage";
import PostAPIService from "../../../services/PostAPIService";
import { Post } from "../../../models/responses/PostDetails";
import SimpleProfileHeader from "../../../components/SimpleProfileHeader";
import PostComments from "./PostComments";
import IconWithText from "../../../components/IconWithText";
import Icon from "../../../components/Icon";
import CreateCommentView from "../../comment/CreateCommentView";
import { useAuth0 } from "@auth0/auth0-react";
import OptionsDialog from "../../../components/OptionsDialog";
import { useAuthSelector } from "../../../redux/slices/authSlice";
import { useDispatch, useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";
import { NotificationType, setNotification } from "../../../redux/slices/notificationSlice";
import { commentSelector, focusComment } from "../../../redux/slices/commentSlice";
import { unstable_batchedUpdates } from 'react-dom';
import PostLikeAPIService from "../../../services/PostLikeAPIService";
import PostLikes from "./PostLikes";
import PostActions from "./PostActions";

const PostView = (props: {
  id: string,
  onDelete: (id: string) => void;
}) => {
  const [post, setPost] = React.useState<Post>();
  const isLikedPost = React.useRef<boolean>(false)
  const postLikes = React.useRef<number>(0)

  const isCreatingComment = useSelector(commentSelector).isCreating

  const {isAuthenticated} = useAuth0()

  const navigate = useNavigate()
  const dispatch = useDispatch()

  React.useEffect(() => {
    PostAPIService.getById(props.id)
    .then((response) => {
      const newPostData: Post = response.data

      setPost(newPostData);
      isLikedPost.current = newPostData.didLoggedUserLikedPost
      postLikes.current = newPostData.likesCount
      
      console.log(response.data);
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
