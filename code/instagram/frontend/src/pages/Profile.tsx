import React, { useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import { useParams } from "react-router-dom";
import Avatar from "../components/common/Avatar";
import IconWithText from "../components/header/IconWithText";
import UserHeader from "../components/profile/UserHeader";
import UserPosts from "../components/profile/UserPosts";
import UserStories from "../components/profile/UserStories";
import { Profile } from "../models/Profile";
import {
  NotificationType,
  setNotification,
} from "../redux/slices/notificationSlice";
import {
  setLatestProfiles,
  userPreferencesSelector,
} from "../redux/slices/userPreferencesSlice";
import UserAPIService from "../services/UserAPIService";
import "../components/profile/Profile.css";

const ProfileView = () => {
  let profileUserId: number = Number(useParams().id);

  const [userProfile, setUserProfile] = React.useState<Profile>();

  const latestProfilesIds = useSelector(
    userPreferencesSelector
  ).latestProfilesIds;

  const dispatch = useDispatch();

  useEffect(() => {
    if (!Number.isInteger(profileUserId)) {
      return;
    }

    UserAPIService.getUserById(profileUserId)
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

  if (!Number.isInteger(profileUserId)) {
    return <h1>Podano niewłaściwe id użytkownika</h1>;
  }

  return (
    <div
      className="profile"
      style={{ display: "grid", rowGap: "50px", padding: "30px 20px 20px 0" }}
    >
      <UserHeader userProfile={userProfile} />
      <UserStories />
      <hr
        style={{
          width: "100%",
          color: "silver",
          border: 0,
          backgroundColor: "silver",
          height: 1,
        }}
      />
      <UserPosts />
    </div>
  );
};

export default ProfileView;
