import { useAuth0 } from "@auth0/auth0-react";
import React from "react";
import { useNavigate } from "react-router-dom";
import IconWithText from "../../components/IconWithText";
import useComponentVisible from "../../components/useComponentVisible";
import NavNonLinkItem from "./NavNonLinkItem";

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
      ref={(ref as any) ? (ref as any) : null}
      style={{ marginTop: "auto", display: "flex", flexDirection: "column" }}
    >
      {isActive && (
        <React.Fragment>
          <div className="nav-menu-container">
            {isAuthenticated && (
              <NavMenuItem
                content="Ustawienia"
                onClick={() => navigate("/settings")}
              />
            )}
          </div>
          <div className="nav-menu-container" style={{ marginTop: 12 }}>
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
        content={<IconWithText iconName="menu" text="Menu" />}
        isActive={isActive}
        onClick={() => setIsActive(!isActive)}
        style={{ marginTop: 12 }}
      />
    </div>
  );
};

export default NavMenu;
