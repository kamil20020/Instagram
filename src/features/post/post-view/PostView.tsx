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

const PostView = (props: { id: string }) => {
  const [post, setPost] = React.useState<Post>();
  const [showOptions, setShowOptions] = React.useState<boolean>(false);

  const loggedUserId = useSelector(useAuthSelector).user?.id

  const {isAuthenticated} = useAuth0()

  const navigate = useNavigate()
  const dispatch = useDispatch()

  const handleShowOptions = (isOpen?: boolean) => {
    setShowOptions(isOpen as boolean)
  }

  const handleCloseShowOptions = () => {
    setShowOptions(false)
  }

  const handleDeletePost = () => {
    handleCloseShowOptions()

    PostAPIService.deletePost(post?.id as string)
    .then(() => {
      dispatch(setNotification({
        type: NotificationType.success,
        message: "Usunięto post"
      }))

      navigate(`/profile/${loggedUserId}`)
    })
  }

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
      <PostImage img={`data:image/png;base64,${post.content}`} />
      <div className="post-details">
        <div className="post-author-info">
          <SimpleProfileHeader
            avatar={post.author.avatar}
            nickname={post.author.nickname}
          />
          <button className="outlined-button" onClick={() => handleShowOptions(true)}>
            <IconWithText iconName="more_horiz" />
          </button>
          <OptionsDialog 
            isOpen={showOptions}
            setIsOpen={handleShowOptions}
            handleClose={handleCloseShowOptions}
            options={
              loggedUserId === post.author.id ? 
                [
                  {name: "Zgłoś", handle: () => console.log("Zgłoś")},
                  {name: "Edytuj", handle: () => console.log("Edytuj")},
                  {name: "Usuń", handle: handleDeletePost}
                ]
              :
                [{name: "Zgłoś", handle: () => console.log("Zgłoś")}]
            }
          />
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
          <h4>Liczba polubień: {!post.areHiddenLikes ? '77 197' : 'Ukryte'}</h4>
            <span style={{ color: "rgb(115, 115, 115)", marginTop: 2 }}>
              {new Date(post.creationDatetime).toLocaleDateString()}
            </span>
          </div>
        </div>
        {!post.areDisabledComments && isAuthenticated && <CreateCommentView postId={props.id} />}
      </div>
    </div>
  );
};

export default PostView;
