import { useAuth0 } from "@auth0/auth0-react";
import React from "react";
import { useNavigate } from "react-router-dom";
import IconWithText from "../../components/IconWithText";
import useComponentVisible from "../../components/useComponentVisible";
import NavNonLinkItem from "./NavNonLinkItem";
import NavMenuItem from "./NavMenuItem";
import Logout from "../../features/auth/Logout";

const NavMenu = () => {
  const { loginWithRedirect, isAuthenticated } = useAuth0();

  const navigate = useNavigate();

  const [isActive, setIsActive] = React.useState<boolean>(false);

  const ref = useComponentVisible(() => setIsActive(false));

  const SettingsItem = () => (
    <div className="nav-menu-container">
      <NavMenuItem content="Ustawienia" onClick={() => navigate("/settings")} />
    </div>
  );

  const LoginItem = () => (
    <NavMenuItem
      content="Logowanie"
      onClick={() =>
        loginWithRedirect({
          authorizationParams: {
            redirect_uri: `${window.location.origin}/?login=${true}`,
          },
        })
      }
    />
  );

  const RegisterItem = () => (
    <NavMenuItem
      content="Rejestracja"
      onClick={() =>
        loginWithRedirect({
          authorizationParams: {
            screen_hint: "signup",
            redirect_uri: `${window.location.origin}/?register=${true}`,
          },
        })
      }
    />
  );

  return (
    <div
      ref={(ref as any) ? (ref as any) : null}
      style={{ marginTop: "auto", display: "flex", flexDirection: "column" }}
    >
      {isActive && (
        <div className="nav-menu-container" style={{ marginTop: 12 }}>
          {!isAuthenticated ? (
            <React.Fragment>
              <LoginItem />
              <RegisterItem />
            </React.Fragment>
          ) : (
            <React.Fragment>
              <SettingsItem />
              <Logout />
            </React.Fragment>
          )}
        </div>
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
