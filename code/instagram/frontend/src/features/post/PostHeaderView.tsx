﻿import { Link, useNavigate } from "react-router-dom";
import IconWithText from "../../components/IconWithText";
import { PostHeader } from "../../models/responses/PostHeader";
import "./Post.css";
import React from "react";

const img1 =
  "https://www.imperiumtapet.com/public/uploads/preview/piekne-widoki-7-3315352142308iyuwjrhvf.jpg";
const img2 =
  "https://img.freepik.com/darmowe-zdjecie/gory-vestrahorn-w-stokksnes-na-islandii_335224-667.jpg?w=1380&t=st=1674737578~exp=1674738178~hmac=c701828364cf1333d4ab301db868dabee2a0231cb886bd09dc709852524ae1d9";
const img3 =
  "https://www.galerie-zdjec.pl/wp-content/uploads/2011/02/piekne-widoki-5.jpg";
const img4 =
  "https://s29.flog.pl/media/foto/13671790_kto-ma-zawsze-pod-gorke-ten-pozniejma-ladne-widoki--.jpg";

const a = {
  FILL: 1,
};

const PostHeaderView = (props: { postHeader: PostHeader }) => {

  const navigate = useNavigate()

  return (
    <div
      className="post-header"
      style={{
        backgroundImage: `url(data:image/png;base64,${props.postHeader.content})`,
      }}
      onClick={() => {
        navigate("", {state: {postId: props.postHeader.id}})
        window.history.pushState(null, '', `/post/${props.postHeader.id}`)
      }}
    >
      <div className="post-header-info">
        <div style={{ display: "flex", justifyContent: "space-between" }}>
          <IconWithText
            iconName={"favorite"}
            text={props.postHeader.likesCount}
            iconStyle={{ color: "white", opacity: 1 }}
            textStyle={{ color: "white", marginRight: 32 }}
          />
          <IconWithText
            iconName={"chat_bubble"}
            text={props.postHeader.commentsCount}
            iconStyle={{ color: "white", opacity: 1 }}
            textStyle={{ color: "white" }}
          />
        </div>
      </div>
    </div>
  );
};

export default PostHeaderView;
