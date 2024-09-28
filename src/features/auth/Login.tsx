import { useAuth0 } from "@auth0/auth0-react";

const Login = () => {

    const {isAuthenticated, user, getAccessTokenSilently} = useAuth0()

    

    return <></>
}

export default Login;