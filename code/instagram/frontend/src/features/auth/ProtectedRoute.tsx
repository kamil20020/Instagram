const ProtectedRoute = (props: {children: JSX.Element}) => {
    console.log(props)
    return (
        {props}
    )
}

export default ProtectedRoute;