import Avatar from "../../../components/Avatar";
import { Post } from "../../../models/Post";

const PostDescription = (props: { post: Post }) => {
  const post = props.post;

  return (
    <div className="comment">
      <Avatar width={40} height={40} image={post.userData.avatar} />
      <div className="comment-details">
        <div>
          <h4 style={{ display: "inline-block", marginRight: 8 }}>
            {post.userData.nickname}
          </h4>
          <span>{post.description}</span>
        </div>
        <div style={{ marginTop: 6 }}>
          <span style={{ color: "#706f6e" }}>
            {new Date(post.creationDatetime).toLocaleDateString()}
          </span>
        </div>
      </div>
    </div>
  );
};

export default PostDescription;
