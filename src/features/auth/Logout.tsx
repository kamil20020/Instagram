import { useAuth0 } from "@auth0/auth0-react"
import { useContext, useEffect } from "react"
import { UserContext } from "../../context/UserContext"
import AuthService from "../../services/AuthService"

const Logout = () => {

    const {isAuthenticated, logout} = useAuth0()

    const userContext = useContext(UserContext)

    useEffect(() => {

        if(isAuthenticated){
            return;
        }

        userContext?.clearUserData()
        AuthService.logout()
    }, [isAuthenticated])

    return (
        <></>
    )
}

export default Logout;