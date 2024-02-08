import React from "react";
import CustomInput from "../components/CustomInput";
import Icon from "../components/Icon";
import InstagramIcon from "../components/InstargamIcon";
import FormService from "../services/FormService";
import UserAPIService from "../services/UserAPIService";
import { useDispatch } from "react-redux";
import { updateUserData } from "../redux/slices/authSlice";

export interface FormProps {
  firstname: string;
  surname: string;
  nickname: string;
}

const initState: FormProps = {
  firstname: "",
  surname: "",
  nickname: "",
};

const FillPersonalData = () => {
  const [form, setForm] = React.useState<FormProps>(initState);

  const [errors, setErrors] = React.useState<FormProps>({ ...initState });

  const dispatch = useDispatch()

  const validate = (): boolean => {
    let occuredError = false;
    let newErrors: FormProps = { ...errors };

    if (!FormService.isNonEmpty(form.firstname)) {
      newErrors.firstname = FormService.requiredMessage;
      occuredError = true;
    }

    if (!FormService.isNonEmpty(form.surname)) {
      newErrors.surname = FormService.requiredMessage;
      occuredError = true;
    }

    if (!FormService.isNonEmpty(form.nickname)) {
      newErrors.nickname = FormService.requiredMessage;
      occuredError = true;
    }

    setErrors({ ...newErrors });

    return !occuredError;
  };

  const handleSave = () => {
    if (!validate()) {
      return;
    }

    UserAPIService.patchLoggedUser(form)
    .then((response) => {
      dispatch(updateUserData(response.data))
    });
  };

  const handleFieldChange = (name: string, value: string) => {
    setForm({ ...form, [name]: value });
    setErrors({ ...errors, [name]: "" });
  };

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
          rowGap: 16,
          padding: 80,
          border: "1px solid silver",
          borderRadius: 6,
        }}
      >
        <InstagramIcon />
        <span style={{ fontSize: 26, marginBottom: 38, marginTop: 20 }}>
          Wypełnij swoje dane
        </span>
        <div
          className="form"
          style={{
            width: "100%",
            display: "flex",
            flexDirection: "column",
            rowGap: 30,
          }}
        >
          <CustomInput
            isRequired
            label="Imię"
            value={form.firstname}
            errorMessage={errors.firstname}
            onChange={(value: string) => handleFieldChange("firstname", value)}
          />
          <CustomInput
            isRequired
            label="Nazwisko"
            value={form.surname}
            errorMessage={errors.surname}
            onChange={(value: string) => handleFieldChange("surname", value)}
          />
          <CustomInput
            isRequired
            label="Pseudonim"
            value={form.nickname}
            errorMessage={errors.nickname}
            onChange={(value: string) => handleFieldChange("nickname", value)}
          />
        </div>
        <button
          className="blue-button-filled"
          style={{ marginTop: 36 }}
          onClick={handleSave}
        >
          Zapisz
        </button>
      </div>
    </div>
  );
};

export default FillPersonalData;
