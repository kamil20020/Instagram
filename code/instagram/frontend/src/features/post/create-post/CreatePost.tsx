import React from "react";
import Dialog from "../../../components/Dialog";
import IconWithText from "../../../components/IconWithText";
import NavNonLinkItem from "../../../layout/header/NavNonLinkItem";
import SelectPostImage from "./SelectPostImage";
import HorizontalLine from "../../profile/HorizontalLine";
import SetPostDetails from "./SetPostDetails";

const CreatePost = () => {
  const [isOpen, setIsOpen] = React.useState<boolean>(false);

  const [img, setImg] = React.useState<string>("");

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
                {img === "" ? (
                  <SelectPostImage setImg={setImg} />
                ) : (
                  <SetPostDetails img={img} />
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

export default CreatePost;
