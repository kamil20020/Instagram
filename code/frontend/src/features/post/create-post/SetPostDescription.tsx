const SetPostDescription = (props: {
  setValue: (newValue: string) => void;
}) => {
  return (
    <div style={{ padding: 22 }}>
      <textarea
        placeholder="Dodaj opis..."
        rows={7}
        maxLength={2000}
        style={{
          border: 0,
          boxSizing: "border-box",
          width: "100%",
          resize: "none",
        }}
        onChange={(event: any) => props.setValue(event.target.value)}
      />
      <h5
        style={{
          fontWeight: "normal",
          color: "gray",
          textAlign: "right",
          margin: 0,
        }}
      >
        10/2000
      </h5>
    </div>
  );
};

export default SetPostDescription;
