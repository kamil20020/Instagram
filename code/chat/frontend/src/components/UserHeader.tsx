import Avatar from "./Avatar"

const UserHeader = (props: {
    nickname: string,
    avatar?: string,
    width?: number,
    height?: number
}) => {

    return (
        <div className="user-header">
            <Avatar avatar={props.avatar} height={props.height} width={props.width}/>
            <h3>{props.nickname}</h3>
        </div>
    )
}

export default UserHeader;