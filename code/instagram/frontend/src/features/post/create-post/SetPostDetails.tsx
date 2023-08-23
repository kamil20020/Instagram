import { useSelector } from "react-redux";
import SimpleProfileHeader from "../../../components/SimpleProfileHeader";
import { RootState } from "../../../redux/rootReducer";
import { useAuthSelector } from "../../../redux/slices/authSlice";
import ProfileHeader from "../../search-users/ProfileHeader";
import SetPostDescription from "./SetPostDescription";
import HorizontalLine from "../../profile/HorizontalLine";

const SetPostDetails = (props: { img: string }) => {
  const loggedUserData = useSelector(useAuthSelector).user;

  return (
    <div
      style={{
        display: "grid",
        height: "100%",
        gridTemplateColumns: "6fr 4fr",
        gridTemplateRows: "1fr",
      }}
    >
      <div
        style={{
          backgroundImage: `url(${props.img})`,
          backgroundSize: "cover",
        }}
      ></div>
      <div>
        <SetPostDescription />
        <HorizontalLine />
        <div style={{ padding: 22 }}></div>
        <HorizontalLine />
        <div style={{ padding: 22 }}></div>
        <HorizontalLine />
        <div style={{ padding: 22 }}></div>
        <HorizontalLine />
        <div
          className="save"
          style={{
            width: "100%",
            display: "flex",
            justifyContent: "center",
            marginTop: 26,
          }}
        >
          <button
            className="blue-button"
            style={{ marginLeft: "auto", marginRight: "auto" }}
          >
            Zapisz
          </button>
        </div>
      </div>
    </div>
  );
};

export default SetPostDetails;
