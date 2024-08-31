import React, { useEffect } from "react";
import UsersDialog from "../../../components/UsersDialog";
import { Post } from "../../../models/responses/PostDetails";
import { UserHeader } from "../../../models/responses/UserHeader";
import PostLikeAPIService from "../../../services/PostLikeAPIService";
import { Pagination } from "../../../models/requests/Pagination";
import { Page } from "../../../models/responses/Page";
import Icon from "../../../components/Icon";
import { focusComment } from "../../../redux/slices/commentSlice";
import { useAuth0 } from "@auth0/auth0-react";
import { setNotification, NotificationType } from "../../../redux/slices/notificationSlice";
import { useDispatch } from "react-redux";
import { PostLikesData } from "../../../models/responses/PostLikesData";
import { unstable_batchedUpdates } from "react-dom";

const PostLikes = (props: {
    post: Post,
    postLikes: number;
    isLikedPost: boolean;
    setIsOpen?: (isOpen: boolean) => void;
    handleClose?: () => void;
}) => {
  const post = props.post

  const [users, setUsers] = React.useState<UserHeader[]>([])
  const [isLikedPost, setIsLikedPost] = React.useState<boolean>(props.isLikedPost)
  const [isDialogOpen, setIsDialogOpen] = React.useState<boolean>(false)

  const {isAuthenticated} = useAuth0()
  
  const dispatch = useDispatch()

  const handleShowLikes = () => {

    if(props.postLikes == 0){
      return;
    }

    const pagination: Pagination = {
      page: 0,
      size: 12
    };

    PostLikeAPIService.getPage(post.id, pagination)
    .then((response) => {
      const convertedResponse: PostLikesData = response.data;
      const newLikes: UserHeader[] = convertedResponse.postLikes.content

      unstable_batchedUpdates(() => {
        setUsers(newLikes)
        setIsDialogOpen(true)
      })
    })
  }

  const handleSwitchPostLike = () => {

    if(isLikedPost){
      PostLikeAPIService.delete(post.id)
      .then(() => {
        dispatch(setNotification({
          type: NotificationType.success,
          message: "Usunięto polubienie"
        }))

        setIsLikedPost(false)
      })
    }
    else{
      PostLikeAPIService.create(post.id)
      .then(() => {
        dispatch(setNotification({
          type: NotificationType.success,
          message: "Dodano polubienie"
        }))

        setIsLikedPost(true)
      });
    }
  }

  return (

    <div className="post-info">
      <div className="post-info-actions">
        {isAuthenticated &&
          <React.Fragment>
            <button className="outlined-button" onClick={handleSwitchPostLike}>
              <Icon iconName="favorite" iconStyle={{color: isLikedPost ? "red" : "black"}}/>
            </button>
            <button className="outlined-button" onClick={() => dispatch(focusComment(undefined))}>
              <Icon iconName="mode_comment" />
            </button>
          </React.Fragment>
        }
      </div>
      <div>
        <h4 className="opacity-hover" onClick={handleShowLikes}>
          Liczba polubień: {!post.areHiddenLikes ? props.postLikes : 'Ukryte'}
        </h4>
        <span style={{ color: "rgb(115, 115, 115)", marginTop: 2}}>
          {new Date(post.creationDatetime).toLocaleDateString()}
        </span>
      </div>
      <UsersDialog
        users={users}
        isOpen={isDialogOpen}
        setIsOpen={setIsDialogOpen}
        handleClose={() => setIsDialogOpen(false)}
      />
    </div>
  )
}

export default PostLikes;