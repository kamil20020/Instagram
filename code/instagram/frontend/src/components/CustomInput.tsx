const CustomInput = (props: {
  label: string;
  value?: string;
  onChange: (value: string) => void;
}) => {
  return (
    <div
      style={{
        display: "flex",
        justifyContent: "space-between",
        alignItems: "center",
        width: "100%",
      }}
    >
      <p style={{fontSize: 18}}>{props.label}</p>
      <input
        type="text"
        value={props.value}
        onChange={(event: any) => props.onChange(event.target.value)}
        style={{padding: 12, borderRadius: 6, border: "1px solid silver"}}
      />
    </div>
  );
};

export default CustomInput;
