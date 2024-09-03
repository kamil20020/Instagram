import React, { useEffect } from "react";
import Avatar from "../../../components/Avatar";
import Icon from "../../../components/Icon";
import UserAPIService from "../../../services/UserAPIService";
import { useDispatch } from "react-redux";
import { NotificationType, setNotification } from "../../../redux/slices/notificationSlice";
import ImgService from "../../../services/ImgService";

const MyAvatar = (props: {
    image?: string;
}) => {

    const [selectedImg, setSelectedImg] = React.useState<string | undefined>(props.image);

    const dispatch = useDispatch()

    useEffect(() => {

        setSelectedImg(props.image)
    }, [props.image])

    const changeProfileImg = (img: string) => {

        UserAPIService.patchLoggedUser({
            avatar: ImgService.fixBase64(img)
        })
        .then(() => {
            dispatch(setNotification({
                type: NotificationType.success,
                message: "Zapisano zdjęcie profilowe"
            }))

            setSelectedImg(img)
        })
        .catch((error) => {
            dispatch(setNotification({
                type: NotificationType.error,
                message: "Nie zapisano zdjęcia profilowego"
            }))
        })
    }

    const chooseImg = () => {
        const input = document.createElement("input");
        input.type = "file";
    
        input.onchange = (e: any) => {
          const file = e.target.files[0];
    
          const reader = new FileReader();
          reader.readAsDataURL(file);
    
          reader.onload = (readerEvent: any) => {
            const img = readerEvent.target.result;
            changeProfileImg(img)
          };
        };
    
        input.click();
    };

    return (
        <div className="my-profile-picture">
            <div className={`change-profile-photo ${selectedImg && 'show-on-hover'}`} onClick={chooseImg}>
                <Icon iconName={"photo_camera"} iconStyle={{fontSize: 100, marginRight: 0}}/>
            </div>
            <Avatar image={selectedImg} width={200} height={200}/>
        </div>
    )
}

export default MyAvatar;