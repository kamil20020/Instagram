import { useAuth0 } from "@auth0/auth0-react";
import NavMenuItem from "../../layout/header/NavMenuItem";
import { useDispatch, useSelector } from "react-redux";
import React from "react";
import UserAPIService from "../../services/UserAPIService";
import { BasicUserData } from "../../models/responses/UserHeader";
import { login, useAuthSelector } from "../../redux/slices/authSlice";
import { useSearchParams } from "react-router-dom";
import AuthService from "../../services/AuthService";
import axios, { AxiosResponse } from "axios";

const Register = () => {
  const { isAuthenticated, user, getAccessTokenSilently } = useAuth0();
  const authData = useSelector(useAuthSelector);
  const dispatch = useDispatch();

  const [searchParams, setSearchParams] = useSearchParams();

  const setAccessToken = async () => {
    const accessToken = await getAccessTokenSilently({
      authorizationParams: {
        audience: "https://instagram.com/"
      },
    });
    AuthService.setRequestsAccessToken(accessToken);
  };

  React.useEffect(() => {
    if (!searchParams.has("register") || !isAuthenticated) {
      return;
    }

    UserAPIService.createUser(user?.sub as string).then((response) => {
      const userId = response.data;
      dispatch(login(userId));

      searchParams.delete("register");
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
    });
  }, [isAuthenticated]);

  return <></>;
};

export default Register;
