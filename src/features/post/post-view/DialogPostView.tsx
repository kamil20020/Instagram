import { useLocation, useNavigate } from "react-router-dom";
import Dialog from "../../../components/Dialog";
import PostView from "./PostView";

const DialogPostView = (props: {
  onDelete: (id: string) => void;
}) => {
  const navigate = useNavigate();

  const location = useLocation();

  const onDelete = (id: string) => {
    props.onDelete(id)
    navigate("", { state: null });
  }

  return (
    <Dialog
      isOpen={true}
      width={70}
      height={90}
      handleClose={() => {
        navigate("", { state: null });
      }}
      content={<PostView id={location.state.postId} onDelete={onDelete}/>}
    />
  );
};

export default DialogPostView;
