import { Link } from "react-router-dom";
import Avatar from "../../components/Avatar";
import { UserProfile } from "../../models/responses/UserProfile";
import { UserHeader } from "../../models/responses/UserHeader";

const ProfileHeader = (props: {
  profile: UserHeader;
  handleClick: (id: string) => void;
}) => {
  const profile = props.profile;

  return (
    <Link
      className="profile-header"
      to={`/profile/${profile.id}`}
      onClick={() => props.handleClick(profile.id)}
    >
      <Avatar 
        image={profile.avatar} 
        width={62} 
        height={62} 
      />
      <div className="profile-header-info">
        <span style={{ fontWeight: "bold" }}>{profile.nickname}</span>
        <span>
          {profile.firstname} {profile.surname}
        </span>
      </div>
    </Link>
  );
};

export default ProfileHeader;
