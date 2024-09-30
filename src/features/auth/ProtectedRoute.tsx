import { useAuth0 } from "@auth0/auth0-react";
import Loading from "../../info/Loading";
import { Outlet } from "react-router";

const ProtectedRoute = () => {

    const {isAuthenticated, isLoading, loginWithRedirect, loginWithPopup} = useAuth0()

    if(isLoading){
        return <Loading/>
    }

    if(!isAuthenticated){

        return (
            <div className="login" style={{height: "100%", display: "flex", justifyContent: "center", alignItems: "center"}}>
                <button
                    style={{color: "black", margin: 0, borderBlockColor: "white"}}
                    onClick={() => loginWithPopup({
                        authorizationParams: {
                            redirect_uri: `${window.location.origin}/?login=${true}`,
                            prompt: "consent"
                        },
                    })}
                >
                    Odśwież
                </button>
            </div>
        )
    }

    // if(!isAuthenticated){
    //     loginWithRedirect({
    //         authorizationParams: {
    //             redirect_uri: `${window.location.origin}/?login=${true}`,
    //         },
    //     },)
    // }

    return (
        <Outlet/>
    )
}

export default ProtectedRoute;