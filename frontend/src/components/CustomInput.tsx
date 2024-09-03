const RequiredSign = () => (
  <span
    style={{
      color: "red",
      position: "absolute",
      marginLeft: 8,
      marginTop: 8,
    }}
  >
    *
  </span>
);

const CustomInput = (props: {
  label: string;
  value?: string;
  errorMessage?: string;
  isRequired?: boolean;
  onChange: (value: string) => void;
}) => {
  return (
    <div className="custom-inputs" style={{ width: "100%" }}>
      <div
        style={{
          display: "flex",
          justifyContent: "space-between",
          alignItems: "center",
          width: "100%",
        }}
      >
        <p style={{ fontSize: 18 }}>{props.label}</p>
        <div>
          <input
            type="text"
            value={props.value}
            onChange={(event: any) => props.onChange(event.target.value)}
            style={{ padding: 12, borderRadius: 6, border: "1px solid silver" }}
          />
          {props.isRequired && <RequiredSign />}
          <span
            style={{
              position: "absolute",
              display: "block",
              marginTop: 6,
              color: "red",
            }}
          >
            {props.errorMessage ? props.errorMessage : " "}
          </span>
        </div>
      </div>
    </div>
  );
};

export default CustomInput;
