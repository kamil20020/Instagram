import Avatar from "./Avatar";

const SimpleProfileHeader = (props: { avatar?: string; nickname: string }) => {
  return (
    <div
      style={{
        display: "flex",
        alignItems: "center",
        padding: 22,
        paddingBottom: 0,
        columnGap: 12,
      }}
    >
      <Avatar image={props.avatar} width={40} height={40} />
      <span style={{ fontWeight: "bold" }}>{props.nickname}</span>
    </div>
  );
};

export default SimpleProfileHeader;
