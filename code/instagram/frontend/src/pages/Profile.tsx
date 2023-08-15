import React, { useEffect } from "react";
import { useDispatch } from "react-redux";
import { useParams } from "react-router-dom";
import UserHeader from "../features/profile/UserHeader";
import UserPosts from "../features/profile/UserPosts";
import UserStories from "../features/profile/UserStories";
import { Profile } from "../models/Profile";
import {
  NotificationType,
  setNotification,
} from "../redux/slices/notificationSlice";
import UserAPIService from "../services/UserAPIService";
import "../features/profile/Profile.css";
import HorizontalLine from "../features/profile/HorizontalLine";

const ProfileView = () => {
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

  return (
    <div
      className="profile"
      style={{ display: "grid", rowGap: "50px", padding: "30px 20px 20px 0" }}
    >
      <UserHeader userProfile={userProfile} />
      <UserStories />
      <HorizontalLine />
      <UserPosts />
    </div>
  );
};

export default ProfileView;
