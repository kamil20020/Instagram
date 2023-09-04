import React, { useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import {
  NotificationType,
  close,
  useNotificationSelector,
} from "../redux/slices/notificationSlice";
import IconWithText from "./IconWithText";
import useComponentVisible from "./useComponentVisible";

const Notification = () => {
  const notification = useSelector(useNotificationSelector);
  const dispatch = useDispatch()

  useEffect(() => {
    if (notification.message === "") {
      return;
    }
    setTimeout(() => {
      dispatch(close());
    }, 4000);
  }, [notification]);

  const ref: any = useComponentVisible(() => dispatch(close()));

  return (
    <React.Fragment>
      {notification.isVisible && (
        <div
          ref={ref}
          style={{
            position: "fixed",
            display: "flex",
            alignItems: "center",
            right: "20px",
            bottom: "20px",
            width: "300px",
            height: "50px",
            borderRadius: "12px",
            padding: "0 22px",
            backgroundColor:
              notification.type === NotificationType.success
                ? "rgb(237, 247, 237)"
                : "red",
            zIndex: 1000
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
