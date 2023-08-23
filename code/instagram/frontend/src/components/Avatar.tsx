const Avatar = (props: {
  image?: string;
  width: number;
  height: number;
}) => {
  const anonymousAvatar =
    "https://thumbs.dreamstime.com/b/default-avatar-profile-icon-vector-unknown-social-media-user-photo-default-avatar-profile-icon-vector-unknown-social-media-user-184816085.jpg";

  const mockedAvatar = "https://1.bp.blogspot.com/-O5dxPSw-z5I/T56EjjoMYnI/AAAAAAAAHr0/XNInjmUb1L4/s1600/100_1187.JPG"

  return (
    <img
      className="profile-photo"
      alt="Zdjęcie profilowe"
      src={props.image ? props.image : mockedAvatar}
      width={props.width}
      height={props.height}
    />
  );
};

export default Avatar;
