import React, { useEffect } from "react"

export interface UserData {
    id: string,
    accountId: string,
    nickname: string,
    firstname: string,
    surname: string,
    avatar?: string
}

export interface UserContextProps {
    user?: UserData,
    setUserData: (user: UserData) => void,
    clearUserData: () => void,
    isUserRegistered: () => boolean,
}

export const UserContext: React.Context<UserContextProps | undefined> =
    React.createContext<UserContextProps | undefined>(undefined)

const UserProvider = (props: {
    children: React.ReactNode
}) => {

    const [user, setUser] = React.useState<UserData | undefined>(() => {

        const savedUserStr = localStorage.getItem("user")

        if(savedUserStr){

            return JSON.parse(savedUserStr)
        }

        return undefined
    })

    useEffect(() => {

        if(!user){
            localStorage.removeItem("user")

            return;
        }
        
        const userStr = JSON.stringify(user ? user : null)

        localStorage.setItem("user", userStr)
    }, [user])

    const setUserData = (user: UserData) => {
        setUser(user)
    }

    const clearUserData = () => {
        setUser(undefined)
    }

    const isUserRegistered = (): boolean => {
        return user !== undefined
    }

    return (
        <UserContext.Provider
            value={{
                user,
                setUserData,
                clearUserData,
                isUserRegistered
            }}
        >
            {props.children}
        </UserContext.Provider>
    )
}

export default UserProvider;