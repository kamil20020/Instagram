const Icon = (props: { iconName: string; iconStyle?: React.CSSProperties }) => {
  return (
    <i
      className="material-symbols-outlined"
      style={{ fontSize: 32, marginRight: 12, ...props.iconStyle }}
    >
      {props.iconName}
    </i>
  );
};

export default Icon;
