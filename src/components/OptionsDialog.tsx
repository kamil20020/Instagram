import Dialog from "./Dialog"

export interface Option {
    name: string;
    handle: () => void;
}

const OptionsDialog = (props: {
    options: Option[];
    isOpen: boolean;
    height?: number;
    width?: number;
    setIsOpen?: (isOpen: boolean) => void;
    handleClose?: () => void;
}) => {

    return (
        <Dialog
            isOpen={props.isOpen}
            height={props.height ? props.height : 7.6 * props.options.length}
            width={props.width ? props.width : 18}
            setIsOpen={props.setIsOpen}
            handleClose={props.handleClose}
            content={
                <div style={{
                    display: "flex", 
                    justifyContent: "center",
                    alignItems: "stretch",
                    width: "100%", 
                    flexDirection: "column"
                }}>
                    {props.options.map((option: Option) => (
                        <h3
                            key={option.name}
                            className="dialog-option"
                            style={{textAlign: "center", paddingTop: 22, paddingBottom: 22}}
                            onClick={option.handle}
                        >
                            {option.name}
                        </h3>
                    ))}
                </div>
            }
        />
    )
}

export default OptionsDialog;