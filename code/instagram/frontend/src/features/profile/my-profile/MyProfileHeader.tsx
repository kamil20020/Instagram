﻿import Avatar from "../../../components/Avatar";
import IconWithText from "../../../components/IconWithText";
import { Profile } from "../../../models/Profile";

const MyProfileHeader = (props: { userProfile: Profile }) => {
  const userProfile = props.userProfile;
  return (
    <div className="user-header">
      <Avatar myAvatar width={240} height={240} />
      <div className="user-details">
        <div className="user-actions">
          <h3 style={{ marginRight: 12 }}>{userProfile?.nickname}</h3>
          <div className="user-actions-buttons">
            <button className="grey-button">Edytuj profil</button>
          </div>
        </div>
        <div className="user-stats">
          <div className="user-stat">
            <h3 className="normal-weight">Posty:&nbsp;</h3>
            <h3>{userProfile?.numberOfPosts}</h3>
          </div>
          <div className="user-stat">
            <h3>{userProfile?.followers}&nbsp;</h3>
            <h3 className="normal-weight">obserwujących</h3>
          </div>
          <div className="user-stat">
            <h3 className="normal-weight">Obserwowani:&nbsp;</h3>
            <h3>{userProfile?.followings}</h3>
          </div>
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
