﻿import { Profile } from "../../../models/Profile";
import HorizontalLine from "../HorizontalLine";
import UserPosts from "../UserPosts";
import UserStories from "../UserStories";
import OtherProfileHeader from "./OtherProfileHeader";

const OtherProfile = (props: {userProfile: Profile}) => {
  return (
    <div
      className="profile"
      style={{ display: "grid", rowGap: "50px", padding: "30px 20px 20px 0" }}
    >
      <OtherProfileHeader userProfile={props.userProfile} />
      <UserStories />
      <HorizontalLine />
      <UserPosts />
    </div>
  );
};

export default OtherProfile;
