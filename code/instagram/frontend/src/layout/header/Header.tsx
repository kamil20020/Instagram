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
              <NavNonLinkItem
                content={<IconWithText iconName="add_box" text="Utwórz" />}
                onClick={() => console.log("Utwórz")}
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
