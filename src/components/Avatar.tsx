import { useSelector } from "react-redux";
import { useAuthSelector } from "../redux/slices/authSlice";

const Avatar = (props: {
  image?: string;
  myAvatar?: boolean;
  width: number;
  height: number;
}) => {
  const anonymousAvatar =
    "https://thumbs.dreamstime.com/b/default-avatar-profile-icon-vector-unknown-social-media-user-photo-default-avatar-profile-icon-vector-unknown-social-media-user-184816085.jpg";

  const userData = useSelector(useAuthSelector).user

  const getImg = () : string => {

    if(props.myAvatar && userData?.avatar){
      return userData?.avatar
    }

    if(props.image){
      return props.image
    }

    return anonymousAvatar
  }

  return (
    <img
      className="profile-photo"
      alt="Zdjęcie profilowe"
      src={getImg()}
      width={props.width}
      height={props.height}
    />
  );
};

//

export default Avatar;
