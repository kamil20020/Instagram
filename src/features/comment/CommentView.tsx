import Avatar from "../../components/Avatar";
import Icon from "../../components/Icon";
import IconWithText from "../../components/IconWithText";
import SimpleProfileHeader from "../../components/SimpleProfileHeader";
import { Comment } from "../../models/responses/CommentData";

const CommentView = (props: { comment: Comment }) => {
  const comment = props.comment;

  return (
    <div className="comment">
      <Avatar width={40} height={40} image={comment.userData.avatar} />
      <div className="comment-details">
        <div>
          <h4 style={{ display: "inline-block", marginRight: 8 }}>
            {comment.userData.nickname}
          </h4>
          <span>{comment.content}</span>
        </div>
        <div style={{ display: "flex", alignItems: "center" }}>
          <span style={{ color: "#706f6e", marginRight: 12 }}>
            {new Date(comment.creationDatetime).toLocaleDateString()}
          </span>
          <button className="grey-button-outlined">Odpowiedz</button>
          <button className="grey-button-outlined show-on-hover">
            <Icon
              iconName="more_horiz"
              iconStyle={{ margin: 0, fontSize: 32, fontWeight: "bold" }}
            />
          </button>
        </div>
      </div>
    </div>
  );
};

export default CommentView;
