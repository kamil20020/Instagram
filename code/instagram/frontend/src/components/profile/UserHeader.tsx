import { Profile } from "../../models/Profile";
import Avatar from "../common/Avatar";
import IconWithText from "../header/IconWithText";

const UserHeader = (props: {userProfile?: Profile}) => {
  const userProfile = props.userProfile
  return (
    <div
      className="user-header"
      style={{
        display: "grid",
        gridTemplateColumns: "1fr 3fr",
        alignItems: "center",
      }}
    >
      <Avatar image={userProfile?.avatar} width={240} height={240} />
      <div
        className="user-details"
        style={{ display: "flex", flexDirection: "column", marginLeft: "80px" }}
      >
        <div
          className="user-actions"
          style={{
            display: "flex",
            justifyContent: "start",
            alignItems: "center",
          }}
        >
          <h3>{userProfile?.nickname}</h3>
          <div
            style={{
              display: "grid",
              gridTemplateColumns: "3fr 3fr 1fr",
              columnGap: "10px",
              padding: "0 10px 0 22px",
            }}
          >
            <button className="grey-button">Obserwowanie</button>
            <button className="grey-button">Wyślij wiadomość</button>
            <button className="grey-button">
              <IconWithText iconName="person_add" />
            </button>
          </div>
          <button className="grey-button">
            <IconWithText iconName="more_horiz" />
          </button>
        </div>
        <div
          className="user-stats"
          style={{
            display: "flex",
            alignItems: "center",
          }}
        >
          <div className="user-stat">
            <h3 className="normal-weight">Posty:&nbsp;</h3>
            <h3>21</h3>
          </div>
          <div className="user-stat">
            <h3>452 tys.&nbsp;</h3>
            <h3 className="normal-weight">obserwujących</h3>
          </div>
          <div className="user-stat">
            <h3 className="normal-weight">Obserwowani:&nbsp;</h3>
            <h3>0</h3>
          </div>
        </div>
        <div
          className="user-info"
          style={{
            display: "flex",
            alignItems: "start",
            flexDirection: "column",
            justifyContent: "center",
          }}
        >
          <h4>
            {userProfile?.firstname} {userProfile?.surname}
          </h4>
          <div className="user-desc">
            E-mail: mail@gmail.com
            <br />
            Twitter: @aaa
          </div>
        </div>
      </div>
    </div>
  );
};

export default UserHeader;
