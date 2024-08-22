import React, { useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import { useParams } from "react-router-dom";
import { UserProfile } from "../models/responses/UserProfile";
import {
  NotificationType,
  setNotification,
} from "../redux/slices/notificationSlice";
import UserAPIService from "../services/UserAPIService";
import "../features/profile/Profile.css";
import MyProfile from "../features/profile/my-profile/MyProfile";
import OtherProfile from "../features/profile/other-profile/OtherProfile";
import { useAuthSelector } from "../redux/slices/authSlice";

const ProfileView = () => {
  let profileUserId = useParams().id;

  const [userProfile, setUserProfile] = React.useState<UserProfile>();

  const loggedUserId = useSelector(useAuthSelector).user?.id
  const dispatch = useDispatch();

  useEffect(() => {
    if (!profileUserId) {
      return;
    }

    UserAPIService.getUserProfileById(profileUserId)
      .then((response) => {
        setUserProfile(response.data);
      })
      .catch((error) => {
        dispatch(
          setNotification({
            type: NotificationType.success,
            message: error.message,
          })
        );
      });
  }, [profileUserId]);

  if (!profileUserId) {
    return <h1>Podano niewłaściwe id użytkownika</h1>;
  }

  if (!userProfile) {
    return <div></div>;
  }

  return (
    <React.Fragment>
      {loggedUserId === profileUserId ? (
        <MyProfile userProfile={userProfile} />
      ) : (
        <OtherProfile userProfile={userProfile} />
      )}
    </React.Fragment>
  );
};

export default ProfileView;
