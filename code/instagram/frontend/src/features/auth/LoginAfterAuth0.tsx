import { useAuth0 } from "@auth0/auth0-react";
import React from "react";

const LoginAfterAuth0 = () => {

    const { isAuthenticated, user } = useAuth0();

    React.useEffect(() => {

        if(isAuthenticated){
            
        }

    }, [isAuthenticated])

    return(
        <></>
    )
}

export default LoginAfterAuth0;