import React, { forwardRef } from "react";

const NavNonLinkItem = forwardRef(
  (
    props: {
      content: any;
      onClick: () => void;
      style?: any;
      isActive?: boolean;
    },
    ref?: any
  ) => {
    return (
      <div
        ref={ref ? ref : null}
        className={props.isActive ? "nav-item nav-item-active" : "nav-item"}
        onClick={props.onClick}
        style={props.style}
      >
        <h1>{props.content}</h1>
      </div>
    );
  }
);

export default NavNonLinkItem;
