import { useSelector, useDispatch } from "react-redux";
import { Link } from "react-router-dom";
import Avatar from "../../components/Avatar";
import { Profile } from "../../models/Profile";
import {
  userPreferencesSelector,
  setLatestProfiles,
} from "../../redux/slices/userPreferencesSlice";

const LatestProfileHeader = (props: {
  profile: Profile;
  handleClick: (id: string) => void;
}) => {
  const profile = props.profile;

  const latestProfilesIds = useSelector(
    userPreferencesSelector
  ).latestProfilesIds;

  const dispatch = useDispatch();

  const handleRemove = () => {

    dispatch(
      setLatestProfiles(
        latestProfilesIds.filter((id: string) => id != profile.id)
      )
    );
  }

  return (
    <Link
      className="profile-header"
      to={`/profile/${profile.id}`}
    >
      <Avatar image={profile.avatar} width={64} height={64} />
      <div className="profile-header-info">
        <span style={{ fontWeight: "bold" }}>{profile.nickname}</span>
        <span>
          {profile.firstname} {profile.surname}
        </span>
      </div>
      <span
        className="material-symbols-outlined remove-button"
        onClick={handleRemove}
      >
        close
      </span>
    </Link>
  );
};

export default LatestProfileHeader;
