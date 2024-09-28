import { useAuth0 } from "@auth0/auth0-react";
import { useContext, useEffect } from "react";
import AuthService from "../../services/AuthService";
import { UserContext } from "../../context/UserContext";
import { useSearchParams } from "react-router-dom";

const Login = () => {

    const {isAuthenticated, user, getAccessTokenSilently} = useAuth0()

    const audience = process.env.REACT_APP_AUTH0_AUDIENCE as string
    const scope = process.env.REACT_APP_AUTH0_SCOPE as string

    const userContext = useContext(UserContext)

    const setAccessToken = async () => {

        const accessToken = await getAccessTokenSilently({
            authorizationParams: {
                audience: audience,
                scope: scope
            }
        })

        AuthService.setAccessToken(accessToken)
    }

    useEffect(() => {

        if(!isAuthenticated){
            return;
        }

        userContext?.setUserData({
            nickname: user?.nickname as string,
            accountId: user?.sub as string,
            avatar: user?.picture
        })

        setAccessToken()

    }, [isAuthenticated])

    return <></>
}

export default Login;