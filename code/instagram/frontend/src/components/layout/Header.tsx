import { useAuth0 } from "@auth0/auth0-react";
import React from "react";
import { Link, NavLink, useNavigate } from "react-router-dom";
import useComponentVisible from "../common/useComponentVisible";
import IconWithText from "../header/IconWithText";
import NavNonLinkItem from "../header/NavNonLinkItem";
import Notifications from "../header/Notifications";
import Search from "../header/Search";

const NavLinkItem = (props: { link: string; content: any }) => {
  return (
    <NavLink
      className={({ isActive }) =>
        isActive ? "nav-item nav-item-active" : "nav-item"
      }
      to={props.link}
    >
      <h1>{props.content}</h1>
    </NavLink>
  );
};

const NavMenuItem = (props: { content: any; onClick: () => void }) => {
  return (
    <div className="nav-menu-item" onClick={props.onClick}>
      {props.content}
    </div>
  );
};

const NavMenu = () => {
  const { loginWithRedirect, isAuthenticated, logout } = useAuth0();

  const navigate = useNavigate();

  const [isActive, setIsActive] = React.useState<boolean>(false);

  const ref = useComponentVisible(() => setIsActive(false));

  return (
    <div
      style={{ marginTop: "auto", display: "flex", flexDirection: "column" }}
    >
      {isActive && (
        <React.Fragment>
          <div
            style={{
              boxShadow:
                "rgba(50, 50, 93, 0.25) 0px 2px 5px -1px, rgba(0, 0, 0, 0.3) 0px 1px 3px -1px",
              borderRadius: 8,
            }}
          >
            {isAuthenticated && (
              <NavMenuItem
                content="Ustawienia"
                onClick={() => navigate("/settings")}
              />
            )}
          </div>
          <div
            style={{
              boxShadow:
                "rgba(50, 50, 93, 0.25) 0px 2px 5px -1px, rgba(0, 0, 0, 0.3) 0px 1px 3px -1px",
              borderRadius: 8,
              marginTop: 12,
            }}
          >
            {!isAuthenticated ? (
              <React.Fragment>
                <NavMenuItem content="Logowanie" onClick={loginWithRedirect} />
                <NavMenuItem
                  content="Rejestracja"
                  onClick={() =>
                    loginWithRedirect({
                      authorizationParams: { screen_hint: "signup" },
                    })
                  }
                />
              </React.Fragment>
            ) : (
              <React.Fragment>
                <NavMenuItem
                  content="Wyloguj"
                  onClick={() =>
                    logout({
                      logoutParams: { returnTo: window.location.origin },
                    })
                  }
                />
              </React.Fragment>
            )}
          </div>
        </React.Fragment>
      )}
      <NavNonLinkItem
        ref={ref}
        content={<IconWithText iconName="menu" text="Menu" />}
        isActive={isActive}
        onClick={() => setIsActive(!isActive)}
        style={{ marginTop: 12 }}
      />
    </div>
  );
};

const Header = () => {
  const { isAuthenticated } = useAuth0();

  return (
    <header
      style={{
        display: "flex",
        flexDirection: "column",
        padding: "33px 16px 20px 16px",
        borderRight: "1px solid silver",
      }}
    >
      <Link
        to="/"
        style={{
          padding: "0 12px 16px 12px",
          fontSize: 32,
          marginBottom: "40px",
        }}
      >
        Instagram
      </Link>
      <nav
        style={{
          display: "flex",
          height: "100%",
          flexDirection: "column",
          alignItems: "stretch",
          rowGap: "20px",
          paddingLeft: 12,
          paddingRight: 12,
        }}
      >
        <NavLinkItem
          link="/"
          content={<IconWithText iconName="home" text="Strona główna" />}
        />
        <Search/>
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
            <Notifications/>
            <NavNonLinkItem
              content={<IconWithText iconName="add_box" text="Utwórz" />}
              onClick={() => console.log("Utwórz")}
            />
          </React.Fragment>
        )}
        <NavMenu />
      </nav>
    </header>
  );
};

export default Header;
