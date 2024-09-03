const NotFound = () => {
  return (
    <div
      style={{
        height: "90vh",
        display: "flex",
        justifyContent: "center",
        alignItems: "center",
      }}
    >
      <i
        className="material-symbols-outlined"
        style={{ fontSize: 64, color: "rgb(200, 0, 0)", marginRight: 12 }}
      >
        error
      </i>
      <h2>Błąd 404 - Nie znaleziono zasobu</h2>
    </div>
  );
};

export default NotFound;
