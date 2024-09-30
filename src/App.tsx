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
import PostViewPage from "./pages/PostViewPage";
import ProtectedRoute from "./features/auth/ProtectedRoute";
import axios from "axios";
import AuthService from "./services/AuthService";
import Chat from "./pages/Chat";
import EmptyChat from "./pages/EmptyChat";

axios.interceptors.request.use(function (config) {
  const token = localStorage.getItem("access_token");

  if(token){
    config.headers.Authorization = AuthService.getAuthorizationHeader(token);
  }
   
  return config;
});

const router = createBrowserRouter(
  createRoutesFromElements(
    <Route
      path="/"
      element={
        <ProtectedRoute>
          <Content />
        </ProtectedRoute>
      }
    >
      <Route path="/" element={<Home />} />
      <Route path="/profile/:id" element={<Profile />} />
      <Route path="/post/:id" element={<PostViewPage />} />
      <Route path="/chat" element={<EmptyChat/>} />
      <Route path="/chat/:accountId" element={<Chat/>} />
      <Route path="*" element={<NotFound />} />
    </Route>
  )
);

function App() {
  return <RouterProvider router={router} />;
}

export default App;
