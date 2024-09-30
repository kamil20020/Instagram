import { useContext } from "react";
import Contacts from "./Contacts";
import { UserContext } from "../../context/UserContext";

const LeftPanel = () => {

    const userNickname = useContext(UserContext)?.user?.nickname

    return (
        <div id="left-panel">
            <h2 style={{marginLeft: 56, marginTop: 36}}>{userNickname}</h2>
            <h3 style={{marginLeft: 56, marginTop: 32}}>Wiadomości</h3>
            <Contacts/>
        </div>
    )
}

export default LeftPanel;