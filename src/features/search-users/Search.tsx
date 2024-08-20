import React, { useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import { Link } from "react-router-dom";
import { UserProfile } from "../../models/responses/UserProfile";
import {
  clearLatestProfiles,
  setLatestProfiles,
  userPreferencesSelector,
} from "../../redux/slices/userPreferencesSlice";
import UserAPIService from "../../services/UserAPIService";
import Avatar from "../../components/Avatar";
import useWindowSize from "../../components/useWindowResize";
import SubPanel from "../../layout/header/SubPanel";
import ProfileHeader from "./ProfileHeader";
import LatestProfileHeader from "./LatestProfileHeader";
import "./Search.css";
import SearchInput from "./SearchInput";
import { UserHeader } from "../../models/responses/UserHeader";
import { Page } from "../../models/responses/Page";

const Search = () => {
  const [criteria, setCriteria] = React.useState<string>("");

  const [profilesTabSize, setProfilesTabSize] = React.useState<number>(0);

  const [profiles, setProfiles] = React.useState<UserProfile[]>([]);

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
      UserAPIService.searchUser(criteria, {page: 0, size: 12})
      .then((response) => {
        const pagedResponse: Page = response.data
        setProfiles(pagedResponse.content);
      });
    } 
    else {
      if (latestProfilesIds.length > 0) {
        UserAPIService.getUsersByIds(latestProfilesIds)
        .then((response) => {
          setProfiles(response.data);
        });
      } 
      else {
        setProfiles([]);
      }
    }
  }, [criteria, latestProfilesIds]);

  const handleClickProfile = (id: string) => {
    let newLatestProfilesIds = [...latestProfilesIds];

    if (newLatestProfilesIds.includes(id)) {
      newLatestProfilesIds = newLatestProfilesIds.filter(
        (id1: string) => id1 != id
      );
    }

    newLatestProfilesIds = [id, ...newLatestProfilesIds];
    dispatch(setLatestProfiles(newLatestProfilesIds));
  };

  return (
    <SubPanel id="search" iconName="search" text="Szukaj">
      <React.Fragment>
        <SearchInput phrase={criteria} setPhrase={setCriteria} />
        <div
          style={{
            borderTop: "1px solid silver",
          }}
        >
          {criteria === "" && (
            <div className="latest-users">
              <h3>Ostatnie</h3>
              {latestProfilesIds.length > 0 && (
                <button
                  className="blue-button"
                  onClick={() => dispatch(clearLatestProfiles())}
                >
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
              ? profiles.map((profile: UserHeader) => (
                  <ProfileHeader
                    key={profile.id}
                    profile={profile}
                    handleClick={handleClickProfile}
                  />
                ))
              : profiles.map((profile: UserHeader) => (
                  <LatestProfileHeader
                    key={profile.id}
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
