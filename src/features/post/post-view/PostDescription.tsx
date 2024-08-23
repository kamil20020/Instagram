import Avatar from "../../../components/Avatar";
import { Post } from "../../../models/responses/PostDetails";

const PostDescription = (props: { post: Post }) => {
  const post = props.post;

  return (
    <div className="comment" style={{marginTop: 0}}>
      <div className="comment-content">
        <Avatar width={40} height={40} image={post.author.avatar} />
        <div className="comment-details">
          <div>
            <h4 style={{ display: "inline-block", marginRight: 8 }}>
              {post.author.nickname}
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
    </div>
  );
};

export default PostDescription;
