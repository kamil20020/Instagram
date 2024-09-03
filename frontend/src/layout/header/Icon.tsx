import { Link } from "react-router-dom";
import InstagramIcon from "../../components/InstargamIcon";

const Icon = () => (
  <Link
    to="/"
    style={{
      padding: "0 12px 16px 12px",
      fontSize: 32,
      marginBottom: "12px",
      marginTop: 8,
    }}
  >
    <InstagramIcon/>
  </Link>
);

export default Icon;
