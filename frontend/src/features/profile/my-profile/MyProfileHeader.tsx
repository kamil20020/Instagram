import { useSelector } from "react-redux";
import Avatar from "../../../components/Avatar";
import { UserProfile } from "../../../models/responses/UserProfile";
import { useAuthSelector } from "../../../redux/slices/authSlice";
import { useEffect } from "react";
import { useAuth0 } from "@auth0/auth0-react";
import MyAvatar from "./MyAvatar";
import UserFollowers from "../UserFollowers";
import UserFollowed from "../UserFollowed";

const MyProfileHeader = (props: { userProfile: UserProfile }) => {

  const userProfile = props.userProfile;

  const { isAuthenticated, user, getAccessTokenSilently, getAccessTokenWithPopup } = useAuth0();

  // useEffect(() => {
  //   aa()
  // }, [])

  // const aa = async () => {
  //   const accessToken = await getAccessTokenSilently({
  //     authorizationParams: {
  //       audience: "https://instagram.com/",
  //       scope: "openid profile email",
  //     },
  //   })
  //   .catch((error) => {
  //     console.log(error)
  //   })
  //   const a = await getAccessTokenWithPopup({
  //     authorizationParams: {
  //       audience: "https://instagram.com/",
  //       scope: "openid profile email",
  //     },
  //   })
  //   console.log(accessToken)
  // }

  return (
    <div className="user-header">
      <MyAvatar image={userProfile.avatar}/>
      <div className="user-details">
        <div className="user-actions">
          <h3 style={{ marginRight: 12 }}>{userProfile?.nickname}</h3>
          {/* <div className="user-actions-buttons">
            <button className="grey-button">Edytuj profil</button>
          </div> */}
        </div>
        <div className="user-stats">
          <div className="user-stat">
            <h3 className="normal-weight">Posty:&nbsp;</h3>
            <h3>{userProfile?.numberOfPosts}</h3>
          </div>
          <UserFollowers userId={userProfile.id} followersCount={userProfile.followers}/>
          <UserFollowed userId={userProfile.id} followedCount={userProfile.followings}/>
        </div>
        <div className="user-info">
          <h4>
            {userProfile?.firstname} {userProfile?.surname}
          </h4>
          <div className="user-desc">{userProfile?.description}</div>
        </div>
      </div>
    </div>
  );
};

export default MyProfileHeader;

/*
<button className="grey-button">Wyświetl archiwum</button>
<button
  className="grey-button"
  style={{ backgroundColor: "white" }}
>
  <IconWithText iconName="settings" />
</button>
*/
