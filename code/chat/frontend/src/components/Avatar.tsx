const Avatar = (props: {
    avatar?: string
    width?: number,
    height?: number
}) => {

    const anonymousAvatar =
        "https://static-00.iconduck.com/assets.00/profile-major-icon-512x512-xosjbbdq.png";

    const avatar = props.avatar

    const getImage = () => {

        if(!avatar){
            return anonymousAvatar
        }

        if(avatar.startsWith("data:image")){
            return avatar
        }

        return `data:image/png;base64,${avatar}`
    }

    return (
        <img 
            src={getImage()} 
            alt="Zdjęcie profilowe" 
            width={props.width ? props.width : 60}
            height={props.height ? props.height : 60}
        />
    )
}

export default Avatar;