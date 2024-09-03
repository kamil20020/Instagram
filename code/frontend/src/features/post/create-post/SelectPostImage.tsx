import Icon from "../../../components/Icon";

const SelectPostImage = (props: { setImg: (img: string) => void }) => {
  const chooseImg = () => {
    const input = document.createElement("input");
    input.type = "file";

    input.onchange = (e: any) => {
      const file = e.target.files[0];

      const reader = new FileReader();
      reader.readAsDataURL(file);

      reader.onload = (readerEvent: any) => {
        const content = readerEvent.target.result;
        props.setImg(content);
      };
    };

    input.click();
  };

  return (
    <div
      style={{
        display: "flex",
        height: "100%",
        flexDirection: "column",
        justifyContent: "center",
        alignItems: "center",
        rowGap: 16,
      }}
    >
      <Icon iconName="image" iconStyle={{ fontSize: 72 }} />
      <span style={{ fontSize: 22, fontWeight: "normal" }}>
        Przeciągnij zdjęcia i filmy tutaj
      </span>
      <button className="blue-button-filled" onClick={chooseImg}>
        Wybierz z komputera
      </button>
    </div>
  );
};

export default SelectPostImage;
