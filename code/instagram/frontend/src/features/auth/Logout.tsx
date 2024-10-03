import { useAuth0 } from "@auth0/auth0-react";
import NavMenuItem from "../../layout/header/NavMenuItem";
import { useDispatch, useSelector } from "react-redux";
import {
  logout as logout1,
  useAuthSelector,
} from "../../redux/slices/authSlice";
import React from "react";
import AuthService from "../../services/AuthService";

const Logout = () => {
  const { logout } = useAuth0();
  const dispatch = useDispatch();

  const urlPostFix = process.env.REACT_APP_URL_POSTFIX

  const handleLogout = () => {

    dispatch(logout1());

    AuthService.removeRequestsAccessToken()
    
    logout({
      logoutParams: { returnTo: `${window.location.origin}/${urlPostFix}` },
    })
  };

  return <NavMenuItem content="Wyloguj" onClick={handleLogout} />;
};

export default Logout;
