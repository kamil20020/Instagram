import { Link } from "react-router-dom";
import { User } from "../../models/User";
import UserHeader from "../../components/UserHeader";

const Contact = (props: {
    contact: User
}) => {

    const contact = props.contact

    return (
        <Link className="contact" to={`/${contact.accountId}`}>
            <UserHeader nickname={contact.nickname} avatar={contact.avatar} height={64} width={64}/>
        </Link>
    )
}

export default Contact;