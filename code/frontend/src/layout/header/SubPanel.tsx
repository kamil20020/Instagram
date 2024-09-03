import React, { useEffect } from "react";
import useComponentVisible from "../../components/useComponentVisible";
import OutsideAlerter from "../../components/useComponentVisible";
import IconWithText from "../../components/IconWithText";
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
        <div ref={ref as any} className="sub-panel">
          <h2 style={{ padding: "12px 14px 36px 24px" }}>{props.text}</h2>
          {props.children}
        </div>
      )}
    </React.Fragment>
  );
};

export default SubPanel;
