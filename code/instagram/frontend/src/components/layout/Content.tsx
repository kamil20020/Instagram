import React from "react";
import { Outlet } from "react-router-dom";
import Search from "../header/Search";
import Footer from "./Footer";
import Header from "./Header";
import "./Layout.css"

const Content = () => {
  return (
    <React.Fragment>
      <div
        style={{
          height: "100vh",
          display: "grid",
          gridTemplateColumns: "1fr 5fr",
          columnGap: 40
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
          <Outlet />
          <Footer />
        </div>
      </div>
    </React.Fragment>
  );
};

export default Content;
