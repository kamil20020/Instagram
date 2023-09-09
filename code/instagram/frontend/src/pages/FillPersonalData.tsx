import React from "react";
import CustomInput from "../components/CustomInput";
import Icon from "../components/Icon";
import InstagramIcon from "../components/InstargamIcon";

export interface FormProps {
  firstname: string;
}

const FillPersonalData = () => {
  const [form, setForm] = React.useState<FormProps>({
    firstname: "",
  });

  return (
    <div
      style={{
        display: "flex",
        justifyContent: "center",
        alignItems: "center",
        height: "100%",
      }}
    >
      <div
        style={{
          display: "flex",
          flexDirection: "column",
          alignItems: "center",
          width: "20%",
          rowGap: 20,
          padding: 80,
          border: "1px solid silver",
          borderRadius: 6,
        }}
      >
        <InstagramIcon />
        <span style={{ fontSize: 26, marginBottom: 38, marginTop: 20 }}>
          Wypełnij swoje dane
        </span>
        <CustomInput
          label="Imię"
          value={form.firstname}
          onChange={(value: string) => setForm({ ...form, firstname: value })}
        />
        <CustomInput
          label="Nazwisko"
          value={form.firstname}
          onChange={(value: string) => setForm({ ...form, firstname: value })}
        />
        <CustomInput
          label="Pseudonim"
          value={form.firstname}
          onChange={(value: string) => setForm({ ...form, firstname: value })}
        />
        <CustomInput
          label="Avatar"
          value={form.firstname}
          onChange={(value: string) => setForm({ ...form, firstname: value })}
        />
        <button className="blue-button-filled" style={{ marginTop: 12 }}>
          Zapisz
        </button>
      </div>
    </div>
  );
};

export default FillPersonalData;
