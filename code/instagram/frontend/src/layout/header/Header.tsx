import { useAuth0 } from "@auth0/auth0-react";
import React from "react";
import { Link, NavLink, useNavigate } from "react-router-dom";
import useComponentVisible from "../../components/useComponentVisible";
import IconWithText from "../../components/IconWithText";
import NavNonLinkItem from "./NavNonLinkItem";
import Notifications from "./Notifications";
import Search from "../../features/search-users/Search";
import NavLinkItem from "./NavLinkItem";
import NavMenu from "./NavMenu";
import Icon from "./Icon";
import { useSelector } from "react-redux";
import { RootState } from "../../redux/rootReducer";
import Avatar from "../../components/Avatar";
import CreatePostView from "../../features/post/create-post/CreatePostView";

const Header = () => {
  const { isAuthenticated } = useAuth0();
  return (
    <header style={{ width: "100%" }}>
      <div className="header">
        <Icon/>
        <nav>
          <NavLinkItem
            link="/"
            content={<IconWithText iconName="home" text="Strona główna" />}
          />
          <Search />
          <NavLinkItem
            link="/explore"
            content={<IconWithText iconName="explore" text="Eksploruj" />}
          />
          {isAuthenticated && (
            <React.Fragment>
              <NavLinkItem
                link="/chat"
                content={<IconWithText iconName="chat" text="Wiadomości" />}
              />
              <Notifications />
              <CreatePostView/>
              <NavLinkItem
                link="/profile/96f2e9fa-d4f0-4067-a272-04cc766f688b/me"
                content={
                  <div style={{display: "flex", alignItems: "center"}}>
                    <Avatar width={32} height={32}/>
                    <p style={{marginLeft: 16}}>Profil</p>
                  </div>
                }
              />
            </React.Fragment>
          )}
          <NavMenu />
        </nav>
      </div>
    </header>
  );
};

export default Header;
