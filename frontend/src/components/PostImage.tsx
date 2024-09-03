const PostImage = (props: { img: string }) => {
  return (
    <div
      style={{
        backgroundImage: `url(${props.img})`,
        backgroundSize: "cover",
        backgroundRepeat: "no-repeat",
        backgroundPosition: "center",
      }}
    ></div>
  );
};

export default PostImage;
