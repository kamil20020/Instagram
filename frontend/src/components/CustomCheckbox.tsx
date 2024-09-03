const CustomCheckbox = (props: {
  label: string;
  isChecked?: boolean;
  setIsChecked: (newValue: boolean) => void;
}) => {
  return (
    <div className="checkbox">
      <span style={{fontWeight: "normal", marginRight: 10}}>{props.label}</span>
      <input
        type="checkbox"
        checked={props.isChecked}
        onChange={() => props.setIsChecked(!props.isChecked)}
      />
    </div>
  );
};

export default CustomCheckbox;
