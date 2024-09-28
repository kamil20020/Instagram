import Contact from "./Contact";

const contacts: number[] = [
    1, 2, 3, 4, 5
]

const Contacts = () => {

    return (
        <div id="contacts">
            <h3 style={{marginLeft: 24}}>Wiadomości</h3>
            {contacts.map((contact: number) => (
                <Contact key={contact} contact={contact}/>
            ))}
        </div>
    )
}

export default Contacts;