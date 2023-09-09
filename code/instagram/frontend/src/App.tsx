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
      <Route
        path="/profile/:id/me"
        element={
          <ProtectedRoute requiresLogin>
            <Profile isMyProfile />
          </ProtectedRoute>
        }
      />
      <Route path="/post/:id" element={<PostViewPage />} />
      <Route path="*" element={<NotFound />} />
    </Route>
  )
);

function App() {
  return <RouterProvider router={router} />;
}

export default App;
