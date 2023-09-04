import React, { useEffect } from "react";
import { useDispatch } from "react-redux";
import { useParams } from "react-router-dom";
import { Profile } from "../models/Profile";
import {
  NotificationType,
  setNotification,
} from "../redux/slices/notificationSlice";
import UserAPIService from "../services/UserAPIService";
import "../features/profile/Profile.css";
import MyProfile from "../features/profile/my-profile/MyProfile";
import OtherProfile from "../features/profile/other-profile/OtherProfile";

const ProfileView = (props: { isMyProfile?: boolean }) => {
  let profileUserId = useParams().id;

  const [userProfile, setUserProfile] = React.useState<Profile>();

  const dispatch = useDispatch();

  useEffect(() => {
    if (!profileUserId) {
      return;
    }

    UserAPIService.getUseProfileById(profileUserId)
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
      {props.isMyProfile ? (
        <MyProfile userProfile={userProfile} />
      ) : (
        <OtherProfile userProfile={userProfile} />
      )}
    </React.Fragment>
  );
};

export default ProfileView;
