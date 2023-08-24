import React from "react";
import Dialog from "../../../components/Dialog";
import IconWithText from "../../../components/IconWithText";
import NavNonLinkItem from "../../../layout/header/NavNonLinkItem";
import SelectPostImage from "./SelectPostImage";
import HorizontalLine from "../../profile/HorizontalLine";
import SetPostDetails, { PostDeails } from "./SetPostDetails";
import PostAPIService, { CreatePost } from "../../../services/PostAPIService";
import { useDispatch } from "react-redux";
import { NotificationType, setNotification } from "../../../redux/slices/notificationSlice";

const CreatePostView = () => {
  const [isOpen, setIsOpen] = React.useState<boolean>(false);

  const [form, setForm] = React.useState<CreatePost>({
    img: "",
    areHiddenLikes: false,
    areDisabledComments: false,
    userId: "96f2e9fa-d4f0-4067-a272-04cc766f688b",
  });

  const dispatch = useDispatch()

  const setImg = (img: string) => {
    setForm({ ...form, img: img });
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
    PostAPIService.createPost({...form, img: PostAPIService.fixBase64(form.img)})
    .then((response) => {
      dispatch(
        setNotification({
          type: NotificationType.success,
          message: "Utworzono post",
        })
      );
      setIsOpen(false)
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
                {form.img === "" ? (
                  <SelectPostImage setImg={setImg} />
                ) : (
                  <SetPostDetails
                    img={form.img}
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
