import { Link } from "react-router-dom";
import Avatar from "./Avatar";

const SimpleProfileHeader = (props: { avatar?: string; nickname: string, userId: string; }) => {
  return (
    <Link
      to={`/profile/${props.userId}`}
      style={{
        display: "flex",
        alignItems: "center",
        padding: 22,
        columnGap: 12,
      }}
    >
      <Avatar image={props.avatar} width={40} height={40} />
      <span style={{ fontWeight: "bold", marginLeft: 6 }}>
        {props.nickname}
      </span>
    </Link>
  );
};

export default SimpleProfileHeader;
