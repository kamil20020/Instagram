import React from "react";
import { Outlet } from "react-router-dom";
import Search from "../features/search-users/Search";
import Footer from "./Footer";
import Header from "./header/Header";
import "./Layout.css"

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
          <div style={{width: "70%"}}>
            <Outlet />
          </div>
          <Footer />
        </div>
      </div>
    </React.Fragment>
  );
};

export default Content;
