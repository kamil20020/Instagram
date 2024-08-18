const Avatar = (props: {
  image?: string;
  width: number;
  height: number;
}) => {
  const anonymousAvatar =
    "https://thumbs.dreamstime.com/b/default-avatar-profile-icon-vector-unknown-social-media-user-photo-default-avatar-profile-icon-vector-unknown-social-media-user-184816085.jpg";

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
    <img
      className="profile-photo"
      alt="Zdjęcie profilowe"
      src={getImg()}
      width={props.width}
      height={props.height}
    />
  );
};

export default Avatar;
