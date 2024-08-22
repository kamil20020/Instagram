import React from "react";
import Dialog from "../../../components/Dialog";
import IconWithText from "../../../components/IconWithText";
import NavNonLinkItem from "../../../layout/header/NavNonLinkItem";
import SelectPostImage from "./SelectPostImage";
import HorizontalLine from "../../profile/HorizontalLine";
import SetPostDetails, { PostDeails } from "./SetPostDetails";
import PostAPIService from "../../../services/PostAPIService";
import { useDispatch } from "react-redux";
import { NotificationType, setNotification } from "../../../redux/slices/notificationSlice";
import { useNavigate } from "react-router-dom";
import { CreatePost } from "../../../models/requests/CreatePost";
import ImgService from "../../../services/ImgService";

const CreatePostView = () => {
  const [isOpen, setIsOpen] = React.useState<boolean>(false);

  const [form, setForm] = React.useState<CreatePost>({
    content: "",
    areHiddenLikes: false,
    areDisabledComments: false
  });

  const dispatch = useDispatch()
  const navigate = useNavigate()

  const setImg = (content: string) => {
    setForm({ ...form, content: content });
  };

  const setPostDetails = (postDetails: PostDeails) => {
    setForm({
      ...form,
      description: postDetails.description,
      areHiddenLikes: postDetails.areHiddenLikes,
      areDisabledComments: postDetails.areDisabledComments,
    });
  };

  const handleSavePost = () => {
    PostAPIService.createPost({...form, content: ImgService.fixBase64(form.content)})
    .then((response) => {
      dispatch(
        setNotification({
          type: NotificationType.success,
          message: "Utworzono post",
        })
      );
      setIsOpen(false)
      //navigate("/profile/")
    })
    .catch((error) => {
      console.log(error)
      setNotification({
        type: NotificationType.error,
        message: "Nie udało się utworzyć posta",
      })
    })
  };

  return (
    <NavNonLinkItem
      content={
        <div className="create-post">
          <IconWithText iconName="add_box" text="Utwórz" />
          <Dialog
            isOpen={isOpen}
            setIsOpen={setIsOpen}
            handleClose={() => setImg("")}
            content={
              <div
                style={{
                  height: "100%",
                  display: "flex",
                  flexDirection: "column",
                }}
              >
                <h1
                  style={{
                    textAlign: "center",
                    paddingTop: 18,
                    paddingBottom: 18,
                  }}
                >
                  Utwórz nowy post
                </h1>
                <HorizontalLine />
                {form.content === "" ? (
                  <SelectPostImage setImg={setImg} />
                ) : (
                  <SetPostDetails
                    img={form.content}
                    postDetails={form}
                    setPostDetails={setPostDetails}
                    savePost={handleSavePost}
                  />
                )}
              </div>
            }
          />
        </div>
      }
      onClick={() => setIsOpen(true)}
    />
  );
};

export default CreatePostView;
