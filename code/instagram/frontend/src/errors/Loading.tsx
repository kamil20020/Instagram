const Loading = () => (
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
      style={{ fontSize: 64, color: "teal", marginRight: 20 }}
    >
      progress_activity
    </i>
    <h2>Ładowanie...</h2>
  </div>
);

export default Loading;