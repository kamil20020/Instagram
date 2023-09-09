import { useAuth0 } from "@auth0/auth0-react";
import NavMenuItem from "../../layout/header/NavMenuItem";
import React from "react";
import { useSelector, useDispatch } from "react-redux";
import { BasicUserData } from "../../models/BasicUserData";
import { useAuthSelector, login } from "../../redux/slices/authSlice";
import UserAPIService from "../../services/UserAPIService";
import { useSearchParams } from "react-router-dom";
import AuthService from "../../services/AuthService";

const Login = () => {
  const { isAuthenticated, user, getAccessTokenSilently } = useAuth0();
  const dispatch = useDispatch();

  const [searchParams, setSearchParams] = useSearchParams();

  const setAccessToken = async () => {
    const accessToken = await getAccessTokenSilently({
      authorizationParams: {
        audience: "https://instagram.com/",
        scope: "openid profile email"
      }
    });
    AuthService.setRequestsAccessToken(accessToken);
  };

  React.useEffect(() => {
    if (!searchParams.has("login") || !isAuthenticated) {
      return;
    }

    UserAPIService.getBasicUserByUserAccountId(user?.sub as string).then(
      (response) => {
        const userData: BasicUserData = response.data;
        dispatch(login(userData));

        searchParams.delete("login");
        setSearchParams(searchParams);

        setAccessToken();
      }
    );
  }, [isAuthenticated]);

  return <></>;
};

export default Login;
