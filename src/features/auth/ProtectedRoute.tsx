import { useAuth0 } from "@auth0/auth0-react";
import Loading from "../../info/Loading";
import { Outlet } from "react-router";

const ProtectedRoute = () => {

    const {isAuthenticated, isLoading, loginWithRedirect} = useAuth0()

    if(isLoading){
        return <Loading/>
    }

    if(!isAuthenticated){
        loginWithRedirect({
            authorizationParams: {
                redirect_uri: `${window.location.origin}/?login=${true}`,
            },
        })
    }

    return (
        <Outlet/>
    )
}

export default ProtectedRoute;