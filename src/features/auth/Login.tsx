import { useAuth0 } from "@auth0/auth0-react";
import { useContext, useEffect } from "react";
import AuthService from "../../services/AuthService";
import { UserContext } from "../../context/UserContext";
import { useSearchParams } from "react-router-dom";
import { useCookies } from "react-cookie";

const Login = () => {

    const {isAuthenticated, user} = useAuth0()

    const userContext = useContext(UserContext)

    useEffect(() => {

        if(!isAuthenticated){
            return;
        }

        userContext?.setUserData({
            nickname: user?.nickname as string,
            accountId: user?.sub as string,
            avatar: user?.picture
        })
    }, [isAuthenticated])

    return <></>
}

export default Login;