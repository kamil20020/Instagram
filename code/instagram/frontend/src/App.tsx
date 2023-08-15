import React from "react";
import logo from "./logo.svg";
import "./App.css";
import {
  createBrowserRouter,
  Outlet,
  Route,
  RouterProvider,
} from "react-router-dom";
import { createRoutesFromElements } from "react-router";
import Home from "./pages/Home";
import Header from "./layout/header/Header";
import Content from "./layout/Content";
import Footer from "./layout/Footer";
import NotFound from "./errors/NotFound";
import Profile from "./pages/Profile";

const router = createBrowserRouter(
  createRoutesFromElements(
    <Route path="/" element={<Content />}>
      <Route path="/" element={<Home />} />
      <Route path="/profile/:id" element={<Profile />} />
      <Route path="*" element={<NotFound />} />
    </Route>
  )
);

function App() {
  return <RouterProvider router={router} />;
}

export default App;
