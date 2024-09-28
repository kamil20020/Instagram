import { Link } from "react-router-dom";

const Contact = (props: {
    contact: string
}) => {

    return (
        <Link className="contact" to={`/${props.contact as string}`}>
            {props.contact}
        </Link>
    )
}

export default Contact;