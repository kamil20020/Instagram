import { useAuth0 } from "@auth0/auth0-react";
import Loading from "../../info/Loading";
import { Outlet } from "react-router";
import AuthService from "../../services/AuthService";
import { useContext } from "react";
import { UserContext } from "../../context/UserContext";

const ProtectedRoute = () => {

    const {isAuthenticated, isLoading, loginWithRedirect, loginWithPopup, getAccessTokenSilently, user} = useAuth0()

    const audience = process.env.REACT_APP_AUTH0_AUDIENCE as string
    const scope = process.env.REACT_APP_AUTH0_SCOPE as string

    if(isLoading){
        return <Loading/>
    }

    const setAccessToken = async () => {

        const accessToken = await getAccessTokenSilently({
            authorizationParams: {
                audience: audience,
                scope: scope,
            },
        })

        console.log("Kamil1")

        AuthService.setAccessToken(accessToken)
    }

    const handleLogin = async () => {

        try{
            await setAccessToken()
        }
        catch(error){
            console.log("Kamil2")
            console.log(error)

            await loginWithPopup({
                authorizationParams: {
                    redirect_uri: `${window.location.origin}/?login=${true}`,
                    prompt: "consent",
                    audience: audience,
                    scope: scope
                },
            })

            setAccessToken()
        }
    }

    if(!isAuthenticated){

        handleLogin()
    }

    return (
        <Outlet/>
    )
}

export default ProtectedRoute;