import Contact from "./Contact";

const contacts: string[] = [
    "1", "2", "3"
]

const Contacts = () => {

    return (
        <div id="contacts">
            {contacts.map((contact: string) => (
                <Contact key={contact} contact={contact}/>
            ))}
        </div>
    )
}

export default Contacts;