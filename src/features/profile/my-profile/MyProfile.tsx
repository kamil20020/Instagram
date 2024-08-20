﻿import { UserProfile } from "../../../models/responses/UserProfile";
import HorizontalLine from "../HorizontalLine";
import UserPosts from "../UserPosts";
import UserStories from "../UserStories";
import MyProfileHeader from "./MyProfileHeader";

const MyProfile = (props: {userProfile: UserProfile}) => {
  return (
    <div
      className="profile"
      style={{ display: "grid", rowGap: "50px", padding: "30px 20px 20px 0" }}
    >
      <MyProfileHeader userProfile={props.userProfile} />
      <HorizontalLine />
      <UserPosts />
    </div>
  );
};

export default MyProfile;
