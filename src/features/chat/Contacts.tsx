import Contact from "./Contact";

const contacts: number[] = [
    1, 2, 3, 4, 5, 6, 7, 8
]

const Contacts = () => {

    return (
        <div id="contacts">
            {contacts.map((contact: number) => (
                <Contact key={contact} contact={contact}/>
            ))}
        </div>
    )
}

export default Contacts;