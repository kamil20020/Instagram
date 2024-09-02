import { UserHeader } from "../models/responses/UserHeader";
import Dialog from "./Dialog";
import SimpleProfileHeader from "./SimpleProfileHeader";

const UsersDialog = (props: {
    users: UserHeader[],
    isOpen: boolean;
    height?: number;
    width?: number;
    setIsOpen?: (isOpen: boolean) => void;
    handleClose?: () => void;
}) => {

    return (
        <Dialog
            isOpen={props.isOpen}
            height={props.height ? props.height : 9.5 * props.users.length}
            width={props.width ? props.width : 18}
            setIsOpen={props.setIsOpen}
            handleClose={props.handleClose}
            content={
                <div
                    style={{
                        display: "flex", 
                        justifyContent: "center",
                        alignItems: "center",
                        flexDirection: "column"
                    }}
                >
                    {props.users.map((user: UserHeader) => (
                        <SimpleProfileHeader avatar={user.avatar} nickname={user.nickname} userId={user.id}/>
                    ))}
                </div>
            }
        />
    );
}

export default UsersDialog;