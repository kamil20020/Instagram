import Avatar from "../../../components/Avatar";
import IconWithText from "../../../components/IconWithText";
import { UserProfile } from "../../../models/responses/UserProfile";

const OtherProfileHeader = (props: {userProfile: UserProfile}) => {
  const userProfile = props.userProfile;

  return (
    <div className="user-header">
      <Avatar image={userProfile?.avatar} width={200} height={200} />
      <div className="user-details">
        <div className="user-actions">
          <h3 style={{ marginRight: 12 }}>{userProfile?.nickname}</h3>
          <div className="user-actions-buttons">
            <button className="blue-button-filled">Obserwuj</button>
            <button className="grey-button">Wyślij wiadomość</button>
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

export default OtherProfileHeader;
//            <button className="grey-button">Obserwowanie</button>


/*
<button className="grey-button">
  <IconWithText iconName="person_add" />
</button>
*/