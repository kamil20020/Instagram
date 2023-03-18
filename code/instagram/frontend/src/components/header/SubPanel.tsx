import React, { useEffect } from "react";
import useComponentVisible from "../common/useComponentVisible";
import OutsideAlerter from "../common/useComponentVisible";
import IconWithText from "../common/IconWithText";
import NavNonLinkItem from "./NavNonLinkItem";

const SubPanel = (props: {
  id: string;
  iconName: string;
  text: string;
  children: JSX.Element;
}) => {
  const [isActive, setIsActive] = React.useState<boolean>(false);

  const ref = useComponentVisible(() => setIsActive(false));

  return (
    <React.Fragment>
      <NavNonLinkItem
        ref={ref}
        content={<IconWithText iconName={props.iconName} text={props.text} />}
        onClick={() => setIsActive(!isActive)}
        isActive={isActive}
      />
      {isActive && (
        <div
          ref={ref as any}
          style={{
            position: "absolute",
            height: "100vh",
            left: "100%",
            minWidth: "140%",
            top: 0,
            zIndex: 1,
            borderRadius: "0 16px 16px 0",
            backgroundColor: "white",
            boxShadow:
              "rgba(0, 0, 0, 0.16) 0px 1px 4px",
          }}
        >
          <h2 style={{padding: "12px 14px 36px 24px"}}>{props.text}</h2>
          {props.children}
        </div>
      )}
    </React.Fragment>
  );
};

export default SubPanel;
