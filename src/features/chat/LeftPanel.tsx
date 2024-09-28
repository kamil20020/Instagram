import Contacts from "./Contacts";

const LeftPanel = () => {

    return (
        <div id="left-panel">
            <h2 style={{marginLeft: 56, marginTop: 36}}>Kamil</h2>
            <h3 style={{marginLeft: 56, marginTop: 28}}>Wiadomości</h3>
            <Contacts/>
        </div>
    )
}

export default LeftPanel;