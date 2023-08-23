const SetPostDescription = () => {
  return (
    <div style={{ padding: 22 }}>
      <textarea
        placeholder="Dodaj opis..."
        style={{
          border: 0,
          boxSizing: "border-box",
          width: "100%",
          resize: "none",
        }}
        rows={10}
        maxLength={2000}
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
