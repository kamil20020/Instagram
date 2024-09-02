import React, { useEffect } from "react";
import { Link } from "react-router-dom";

const Avatar = (props: {
  image?: string;
  width: number;
  height: number;
  userId?: string;
}) => {
  const anonymousAvatar =
    "https://static-00.iconduck.com/assets.00/profile-major-icon-512x512-xosjbbdq.png";

  const getImg = () => {

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
        width={props.width}
        height={props.height}
      />
    </Link>
  );
};

export default Avatar;
