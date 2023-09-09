import React from "react";
import { Outlet } from "react-router-dom";
import Search from "../features/search-users/Search";
import Footer from "./Footer";
import Header from "./header/Header";
import "./Layout.css";
import Login from "../features/auth/Login";
import Register from "../features/auth/Register";
import Notification from "../components/Notification";

const Content = () => {
  return (
    <React.Fragment>
      <div
        style={{
          height: "100vh",
          display: "grid",
          gridTemplateColumns: "1fr 5fr",
        }}
      >
        <Header />
        <div
          style={{
            display: "flex",
            alignItems: "center",
            flexDirection: "column",
          }}
        >
          <div style={{ width: "70%" }}>
            <Outlet />
          </div>
          <Footer />
        </div>
      </div>
      <Login />
      <Register />
      <Notification/>
    </React.Fragment>
  );
};

export default Content;
