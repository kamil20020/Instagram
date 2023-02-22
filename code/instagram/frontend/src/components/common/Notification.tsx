import React, { useEffect } from "react";
import { useSelector } from "react-redux";
import {
  NotificationType,
  useNotificationSelector,
} from "../../redux/slices/notificationSlice";
import IconWithText from "../header/IconWithText";
import useComponentVisible from "./useComponentVisible";

const Notification = () => {
  const notification = useSelector(useNotificationSelector);

  const [visible, setVisible] = React.useState<boolean>(false);

  useEffect(() => {
    if (notification.message === "") {
      return;
    }
    setVisible(true);
    setTimeout(() => {
      setVisible(false);
    }, 4000);
  }, [notification]);

  const ref: any = useComponentVisible(() => setVisible(false));

  return (
    <React.Fragment>
      {visible && (
        <div
          ref={ref}
          style={{
            position: "absolute",
            display: "flex",
            alignItems: "center",
            right: "10px",
            bottom: "10px",
            width: "20%",
            height: "10%",
            borderRadius: "4px",
            backgroundColor:
              notification.type === NotificationType.success
                ? "rgb(237, 247, 237)"
                : "red",
          }}
        >
          <IconWithText
            iconName="task_alt"
            text={notification.message}
            iconStyle={{
              color:
                notification.type === NotificationType.success
                  ? "rgb(46, 125, 50)"
                  : "red",
            }}
          />
        </div>
      )}
    </React.Fragment>
  );
};

export default Notification;
