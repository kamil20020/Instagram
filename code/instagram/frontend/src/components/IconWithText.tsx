const IconWithText = (props: {
  iconName: string;
  text?: string;
  iconStyle?: React.CSSProperties;
  textStyle?: React.CSSProperties;
}) => {
  return (
    <div style={{ display: "flex", alignItems: "center" }}>
      <i
        className="material-symbols-outlined"
        style={{ fontSize: 32, marginRight: 12, ...props.iconStyle }}
      >
        {props.iconName}
      </i>
      <p style={props.textStyle}>{props.text}</p>
    </div>
  );
};

export default IconWithText;
