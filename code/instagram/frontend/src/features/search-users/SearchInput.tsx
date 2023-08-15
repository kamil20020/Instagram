import React from "react";

const SearchInput = (props: {
  phrase: string;
  setPhrase: (newPhrase: string) => void;
}) => {
  return (
    <div style={{ display: "grid" }}>
      <input
        className="search-input"
        type="text"
        placeholder="Szukaj"
        aria-label="Pole wyjściowe wyszukiwania"
        value={props.phrase}
        onChange={(event: any) => props.setPhrase(event.target.value)}
      />
      <span
        className="material-symbols-outlined cancel-button"
        onClick={() => props.setPhrase("")}
      >
        cancel
      </span>
    </div>
  );
};

export default SearchInput;
