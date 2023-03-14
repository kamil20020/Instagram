const IconWithText = (props: { iconName: string; text?: string, iconStyle?: React.CSSProperties }) => {
    return (
      <div style={{ display: "flex", alignItems: "center"}}>
        <i
          className="material-symbols-outlined"
          style={{ fontSize: 28, marginRight: 12, ...props.iconStyle }}
        >
          {props.iconName} 
        </i>
        <p>
        {props.text}
        </p>
      </div>
    );
};

export default IconWithText;