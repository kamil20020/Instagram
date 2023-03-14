const Avatar = (props: {
  image: string | undefined;
  width: number;
  height: number;
}) => {
  const anonymousAvatar =
    "https://thumbs.dreamstime.com/b/default-avatar-profile-icon-vector-unknown-social-media-user-photo-default-avatar-profile-icon-vector-unknown-social-media-user-184816085.jpg";

  return (
    <img
      className="profile-photo"
      alt="Zdjęcie profilowe"
      src={props.image ? props.image : anonymousAvatar}
      width={props.width}
      height={props.height}
    />
  );
};

export default Avatar;
