import Avatar from "./Avatar";

const SimpleProfileHeader = (props: { avatar?: string; nickname: string }) => {
  return (
    <div className="simple-profile-header">
      <Avatar image={props.avatar} width={64} height={64} />
      <div className="profile-header-info">
        <span style={{ fontWeight: "bold" }}>{props.nickname}</span>
      </div>
    </div>
  );
};

export default SimpleProfileHeader;
