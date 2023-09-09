import React from "react";
import { useSelector } from "react-redux";
import { useAuthSelector } from "../../redux/slices/authSlice";
import Login from "./Login";
import { useAuth0 } from "@auth0/auth0-react";
import Loading from "../../errors/Loading";
import FillPersonalData from "../../pages/FillPersonalData";

const ProtectedRoute = (props: {
  requiresLogin?: boolean;
  children: JSX.Element;
}) => {
  const { isLoading, loginWithRedirect } = useAuth0();

  const authData = useSelector(useAuthSelector)
  const isLogged = authData.isAuthenticated
  const loggedUserData = authData.user

  if (isLoading) {
    return <Loading />;
  }

  if(isLogged && !loggedUserData?.nickname){
    return <FillPersonalData/>
  }

  if (props.requiresLogin && !isLogged) {
    loginWithRedirect({
      authorizationParams: {
        redirect_uri: `${window.location.origin}/?login=${true}`,
      },
    });
    return <></>;
  }

  return <React.Fragment>{props.children}</React.Fragment>;
};

export default ProtectedRoute;
