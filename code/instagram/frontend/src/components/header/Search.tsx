import React, { useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import { Link } from "react-router-dom";
import { Profile } from "../../models/Profile";
import {
  clearLatestProfiles,
  setLatestProfiles,
  userPreferencesSelector,
} from "../../redux/slices/userPreferencesSlice";
import UserAPIService from "../../services/UserAPIService";
import Avatar from "../common/Avatar";
import useWindowSize from "../common/useWindowResize";
import SubPanel from "./SubPanel";

const ProfileHeader = (props: {
  profile: Profile;
  handleClick: (id: number) => void;
}) => {
  const profile = props.profile;

  return (
    <Link
      className="profile-header"
      to={`/profile/${profile.userId}`}
      onClick={() => props.handleClick(profile.userId)}
    >
      <Avatar image={profile.avatar} width={64} height={64}/>
      <div
        style={{
          display: "flex",
          flexDirection: "column",
          justifyContent: "center",
          marginLeft: 4,
        }}
      >
        <span style={{ fontWeight: "bold" }}>{profile.nickname}</span>
        <span>
          {profile.firstname} {profile.surname}
        </span>
      </div>
    </Link>
  );
};

const LatestProfileHeader = (props: {
  profile: Profile;
  handleClick: (id: number) => void;
}) => {

  const profile = props.profile;

  const latestProfilesIds = useSelector(
    userPreferencesSelector
  ).latestProfilesIds;

  const dispatch = useDispatch();

  let removed: boolean = false

  return (
    <Link
      className="profile-header"
      to={`/profile/${profile.userId}`}
      onClick={() => {
        if(!removed){
          props.handleClick(profile.userId)
        }
      }}
    >
      <Avatar image={profile.avatar} width={64} height={64}/>
      <div
        style={{
          display: "flex",
          flexDirection: "column",
          justifyContent: "center",
          marginLeft: 4,
        }}
      >
        <span style={{ fontWeight: "bold" }}>{profile.nickname}</span>
        <span>
          {profile.firstname} {profile.surname}
        </span>
      </div>
      <span
        className="material-symbols-outlined"
        onClick={() => {
          dispatch(
            setLatestProfiles(
              latestProfilesIds.filter((id: number) => id != profile.userId)
            )
          );
          removed = true
        }}
      >
        close
      </span>
    </Link>
  );
};

const Search = () => {
  const [criteria, setCriteria] = React.useState<string>("");

  const [profilesTabSize, setProfilesTabSize] = React.useState<number>(0);

  const [profiles, setProfiles] = React.useState<Profile[]>([]);

  const latestProfilesIds = useSelector(
    userPreferencesSelector
  ).latestProfilesIds;

  const dispatch = useDispatch();

  useWindowSize();

  useEffect(() => {
    setProfilesTabSize(window.innerHeight - (criteria === "" ? 284 : 214));
  }, [window.innerHeight]);

  useEffect(() => {
    if (criteria !== "") {
      UserAPIService.searchUser(criteria).then((response) => {
        setProfiles(response.data);
      });
    } else {
      if (latestProfilesIds.length > 0) {
        UserAPIService.getUsersByIds(latestProfilesIds).then((response) => {
          setProfiles(response.data);
        });
      } else {
        setProfiles([]);
      }
    }
  }, [criteria, latestProfilesIds]);

  const handleClickProfile = (id: number) => {
    let newLatestProfilesIds = [...latestProfilesIds];

    if (newLatestProfilesIds.includes(id)) {
      newLatestProfilesIds = newLatestProfilesIds.filter(
        (id1: number) => id1 != id
      );
    }

    newLatestProfilesIds = [id, ...newLatestProfilesIds];
    dispatch(setLatestProfiles(newLatestProfilesIds));
  };

  return (
    <SubPanel id="search" iconName="search" text="Szukaj">
      <React.Fragment>
        <div style={{ display: "grid" }}>
          <input
            className="search-input"
            type="text"
            placeholder="Szukaj"
            aria-label="Pole wyjściowe wyszukiwania"
            value={criteria}
            onChange={(event: any) => setCriteria(event.target.value)}
          />
          <span
            className="material-symbols-outlined"
            onClick={() => setCriteria("")}
            style={{
              position: "absolute",
              marginTop: "10px",
              right: "28px",
              opacity: 0.4,
            }}
          >
            cancel
          </span>
        </div>
        <div
          style={{
            borderTop: "1px soluserId silver",
          }}
        >
          {criteria === "" && (
            <div
              style={{
                display: "flex",
                alignItems: "center",
                justifyContent: "space-between",
                margin: "6px 24px 2px 24px",
              }}
            >
              <h3>Ostatnie</h3>
              {latestProfilesIds.length > 0 && (
                <button className="blue-button" onClick={() => dispatch(clearLatestProfiles())}>
                  Wyczyść wszystko
                </button>
              )}
            </div>
          )}
          <div
            style={{
              maxHeight: `${profilesTabSize}px`,
              paddingTop: "12px",
              overflowY: "auto",
            }}
          >
            {criteria !== ""
              ? profiles.map((profile: Profile) => (
                  <ProfileHeader
                    key={profile.userId}
                    profile={profile}
                    handleClick={handleClickProfile}
                  />
                ))
              : profiles.map((profile: Profile) => (
                  <LatestProfileHeader
                    key={profile.userId}
                    profile={profile}
                    handleClick={handleClickProfile}
                  />
                ))}
          </div>
        </div>
      </React.Fragment>
    </SubPanel>
  );
};

export default Search;
