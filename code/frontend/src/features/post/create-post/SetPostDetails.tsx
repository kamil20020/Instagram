import { useSelector } from "react-redux";
import SimpleProfileHeader from "../../../components/SimpleProfileHeader";
import { RootState } from "../../../redux/rootReducer";
import { useAuthSelector } from "../../../redux/slices/authSlice";
import ProfileHeader from "../../search-users/ProfileHeader";
import SetPostDescription from "./SetPostDescription";
import HorizontalLine from "../../profile/HorizontalLine";
import React from "react";
import CustomCheckbox from "../../../components/CustomCheckbox";
import PostImage from "../../../components/PostImage";

export interface PostDeails {
  description?: string;
  areHiddenLikes: boolean;
  areDisabledComments: boolean;
}

const SetPostDetails = (props: {
  img: string;
  postDetails: PostDeails;
  setPostDetails: (postDetails: PostDeails) => void;
  savePost: () => void;
}) => {
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
      <PostImage img={props.img}/>
      <div>
        <SimpleProfileHeader 
          nickname={loggedUserData?.nickname as string} 
          userId={loggedUserData?.id as string}
        />
        <SetPostDescription
          setValue={(newValue: string) =>
            props.setPostDetails({
              ...props.postDetails,
              description: newValue,
            })
          }
        />
        <HorizontalLine />
        <div style={{ padding: 22 }}>
          <CustomCheckbox
            label="Ukryj liczbę polubień"
            setIsChecked={(isChecked: boolean) =>
              props.setPostDetails({
                ...props.postDetails,
                areHiddenLikes: isChecked,
              })
            }
          />
        </div>
        <HorizontalLine />
        <div style={{ padding: 22 }}>
          <CustomCheckbox
            label="Wyłącz komentarze"
            setIsChecked={(isChecked: boolean) =>
              props.setPostDetails({
                ...props.postDetails,
                areDisabledComments: isChecked,
              })
            }
          />
        </div>
        <HorizontalLine />
        <div
          className="save"
          style={{
            width: "100%",
            display: "flex",
            justifyContent: "center",
            marginTop: 30,
          }}
        >
          <button
            className="blue-button"
            style={{ marginLeft: "auto", marginRight: "auto" }}
            onClick={props.savePost}
          >
            Zapisz
          </button>
        </div>
      </div>
    </div>
  );
};

export default SetPostDetails;
