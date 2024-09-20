import useComponentVisible from "./useComponentVisible";

const Dialog = (props: {
  content: React.ReactNode;
  isOpen: boolean;
  height?: number;
  width?: number;
  setIsOpen?: (isOpen: boolean) => void;
  handleClose?: () => void;
}) => {
  const ref = useComponentVisible(() => {
    if (props.handleClose) {
      props.handleClose();
    }

    if(props.setIsOpen){
      props.setIsOpen(false);
    }
  }) as any;

  if (!props.isOpen) {
    return <></>;
  }

  return (
    <div
      className="dialog"
      style={{
        position: "fixed",
        display: "flex",
        justifyContent: "center",
        alignItems: "center",
        left: 0,
        top: 0,
        width: "100vw",
        height: "100vh",
        backgroundColor: "rgba(0, 0, 0, 0.6)",
        zIndex: 10
      }}
    >
      <div
        ref={ref ? ref : null}
        style={{
          width: `${props.width ? props.width : 60}%`,
          height:`${props.height ? props.height : 70}%`,
          backgroundColor: "white",
          borderRadius: 12,
          zIndex: 20,
        }}
      >
        {props.content}
      </div>
    </div>
  );
};

export default Dialog;
