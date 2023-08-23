import useComponentVisible from "./useComponentVisible";

const Dialog = (props: {
  content: React.ReactNode;
  isOpen: boolean;
  setIsOpen: (isOpen: boolean) => void;
  handleClose?: () => void;
}) => {
  const ref = useComponentVisible(() => {
    if (props.handleClose) {
      props.handleClose();
    }
    props.setIsOpen(false);
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
      }}
    >
      <div
        ref={ref ? ref : null}
        style={{
          width: "60%",
          height: "70%",
          backgroundColor: "white",
          borderRadius: 12,
          zIndex: 2,
        }}
      >
        {props.content}
      </div>
    </div>
  );
};

export default Dialog;
