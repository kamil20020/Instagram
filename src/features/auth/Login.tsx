import { useAuth0 } from "@auth0/auth0-react";
import { useContext, useEffect } from "react";
import AuthService from "../../services/AuthService";
import { UserContext } from "../../context/UserContext";
import { useSearchParams } from "react-router-dom";
import { useCookies } from "react-cookie";

const Login = () => {

    const {isAuthenticated, user, getAccessTokenSilently, loginWithRedirect, getAccessTokenWithPopup} = useAuth0()

    const messagesAudience = process.env.REACT_APP_AUTH0_MESSAGES_AUDIENCE as string
    const usersAudience = process.env.REACT_APP_AUTH0_USERS_AUDIENCE as string
    const scope = process.env.REACT_APP_AUTH0_SCOPE as string

    const parentUrl = process.env.REACT_APP_PARENT_URL as string

    const userContext = useContext(UserContext)

    const setUsersAccessToken = async () => {

        try{
            const accessToken = await getAccessTokenSilently({
                authorizationParams: {
                    audience: usersAudience,
                    scope: scope,
                },
            })

            console.log("Kamil1")
    
            AuthService.setUsersAccessToken(accessToken)
        }
        catch(error){
            console.log("Kamil2")
            console.log(error)
        }
    } 

    const setMessagesAccessToken = async () => {

        const accessToken = await getAccessTokenSilently({
            authorizationParams: {
                audience: messagesAudience,
                scope: scope
            },
        })

        AuthService.setMessagesAccessToken(accessToken)
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

        setMessagesAccessToken()
        setUsersAccessToken()

    }, [isAuthenticated])

    return <></>
}

export default Login;