import { Link } from "react-router-dom";

const Avatar = (props: {
  image?: string;
  width: number;
  height: number;
  userId?: string;
}) => {
  const anonymousAvatar =
    "https://www.pngall.com/wp-content/uploads/5/Profile-PNG-File.png";

  const getImg = () : string => {

    if(props.image){

      if(props.image.startsWith("data:image")){
        return props.image
      }

      return "data:image/png;base64," + props.image
    }

    return anonymousAvatar
  }

  return (
    <Link to={props.userId ? `/profile/${props.userId}` : ""} >
      <img
        className="profile-photo"
        alt="Zdjęcie profilowe"
        src={getImg()}
        width={props.image ? props.width : props.width + 40}
        height={props.image ? props.height : props.height + 40}
      />
    </Link>
  );
};

export default Avatar;
