import { useAuth0 } from "@auth0/auth0-react";
import Avatar from "../../../components/Avatar";
import IconWithText from "../../../components/IconWithText";
import { UserProfile } from "../../../models/responses/UserProfile";
import UserFollowed from "../UserFollowed";
import UserFollowers from "../UserFollowers";
import FollowUser from "./FollowUser";
import { useNavigate } from "react-router-dom";

const OtherProfileHeader = (props: {userProfile: UserProfile}) => {
  const userProfile = props.userProfile;

  const {isAuthenticated} = useAuth0()

  const navigate = useNavigate()

  const handleRedirectToMessenger = () => {

    navigate(`/chat/${userProfile.accountId}`)
  }

  return (
    <div className="user-header" style={{marginBottom: 12}}>
      <Avatar image={userProfile?.avatar} width={200} height={200} />
      <div className="user-details">
        <div className="user-actions">
          <h3 style={{ marginRight: 12 }}>{userProfile?.nickname}</h3>
          <div className="user-actions-buttons">
            <FollowUser userId={userProfile.id} doesFollow={userProfile.didLoggedUserFollow}/>
            {isAuthenticated && 
              <button 
                className="grey-button"
                onClick={handleRedirectToMessenger}
              >
                Wyślij wiadomość
              </button>
            }
          </div>
          {/* <button className="outlined-button" style={{ marginLeft: 4 }}>
            <IconWithText iconName="more_horiz" />
          </button> */}
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

export default OtherProfileHeader;
//            <button className="grey-button">Obserwowanie</button>


/*
<button className="grey-button">
  <IconWithText iconName="person_add" />
</button>
*/