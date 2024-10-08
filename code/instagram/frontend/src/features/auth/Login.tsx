﻿import { useAuth0 } from "@auth0/auth0-react";
import NavMenuItem from "../../layout/header/NavMenuItem";
import React from "react";
import { useSelector, useDispatch } from "react-redux";
import { UserHeader } from "../../models/responses/UserHeader";
import { useAuthSelector, login } from "../../redux/slices/authSlice";
import UserAPIService from "../../services/UserAPIService";
import { useSearchParams } from "react-router-dom";
import AuthService from "../../services/AuthService";
import axios, { AxiosResponse } from "axios";

const Login = () => {
  const { isAuthenticated, user, getAccessTokenSilently } = useAuth0();
  const dispatch = useDispatch();

  const [searchParams, setSearchParams] = useSearchParams();

  const audience = process.env.REACT_APP_AUTH0_AUDIENCE
  const scope = process.env.REACT_APP_AUTH0_SCOPE

  const setAccessToken = async () => {
    const accessToken = await getAccessTokenSilently({
      authorizationParams: {
        audience: audience,
        scope: scope,
      },
    });
    console.log(accessToken)
    AuthService.setRequestsAccessToken(accessToken);
  };

  React.useEffect(() => {
    if (!searchParams.has("login") || !isAuthenticated) {
      return;
    }

    UserAPIService.getUserBasicInfoByUserAccountId(user?.sub as string).then(
      (response) => {
        const userData: UserHeader = response.data;
        dispatch(login(userData));

        searchParams.delete("login");
        setSearchParams(searchParams);

        setAccessToken();

        axios.interceptors.response.use(
          (response: AxiosResponse<any, any>) => {
            if (response.status === 401) {
              console.log("Elapsed");
            }

            return response;
          },
          (error: any) => {
            return Promise.reject(error.message);
          }
        );
      }
    );
  }, [isAuthenticated]);

  return <></>;
};

export default Login;
