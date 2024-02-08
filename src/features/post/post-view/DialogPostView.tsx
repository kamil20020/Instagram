import { useLocation, useNavigate } from "react-router-dom";
import Dialog from "../../../components/Dialog";
import PostView from "./PostView";

const DialogPostView = () => {
  const navigate = useNavigate();

  const location = useLocation();

  return (
    <Dialog
      isOpen={true}
      width={70}
      height={90}
      handleClose={() => {
        navigate("", { state: null });
      }}
      content={<PostView id={location.state.postId} />}
    />
  );
};

export default DialogPostView;
