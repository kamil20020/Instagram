import { useEffect, useState } from "react";
import Contact from "./Contact";
import SavedMessageService from "../../services/SavedMessageService";
import { Pagination } from "../../models/Pagination";
import { User } from "../../models/User";
import { Page } from "../../models/Page";
import UserService from "../../services/UserService";
import { useAuth0 } from "@auth0/auth0-react";

const Contacts = () => {

    const {isAuthenticated, user} = useAuth0()

    const [latestUsers, setLatestUsers] = useState<User[]>([])

    useEffect(() => {

        if(!isAuthenticated){
            return;
        }

        const pagination: Pagination = {
            page: 0,
            size: 5
        }

        SavedMessageService.getLatestUsers(pagination)
        .then((response) => {

            const latestUsersAccountsIds: string[] = response.data

            if(latestUsersAccountsIds.length == 0){
                return;
            }

            UserService.getUsersHeadersByAccountsIds(latestUsersAccountsIds)
            .then((response) => {
                setLatestUsers(response.data)
            })

        })
        .catch((error) => {
            console.log(error)
        })
    }, [isAuthenticated, user])

    return (
        <div id="contacts">
            {latestUsers.map((latestUser: User) => (
                <Contact key={latestUser.accountId} contact={latestUser}/>
            ))}
        </div>
    )
}

export default Contacts;