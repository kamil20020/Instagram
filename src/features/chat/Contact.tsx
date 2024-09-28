const Contact = (props: {
    contact: number
}) => {

    return (
        <div className="contact">
            {props.contact}
        </div>
    )
}

export default Contact;