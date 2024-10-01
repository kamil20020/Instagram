import React, { createContext, useEffect, useRef, useState } from 'react';
import logo from './logo.svg';
import './App.css';
import { createRoutesFromElements } from "react-router";
import {
  BrowserRouter,
  createBrowserRouter,
  Outlet,
  Route,
  RouterProvider,
} from "react-router-dom";
import { Client } from '@stomp/stompjs';
import { Message } from './models/Message';
import ProtectedRoute from './features/auth/ProtectedRoute';
import Chat from './pages/Chat';
import axios from 'axios';
import AuthService from './services/AuthService';
import UserProvider, { UserContext } from './context/UserContext';
import Login from './features/auth/Login';
import EmptyChat from './pages/EmptyChat';
import Logout from './features/auth/Logout';

axios.interceptors.request.use((request) => {

  let accessToken = ""

  accessToken = AuthService.getAccessToken() as string

  if(accessToken){
    request.headers.Authorization = AuthService.getAuthorizationBearerHeader(accessToken)
  }

  return request
})

const urlPostFix = process.env.REACT_APP_URL_POSTFIX

function App() {

  const router = createBrowserRouter(
    createRoutesFromElements(
      <Route
        path="/"
        element={<ProtectedRoute/>}
      >
        <Route index element={<EmptyChat/>}/>
        <Route path=":accountId" element={<Chat/>}/>
      </Route>
    ),
    {
      basename: `${window.location.origin}/${urlPostFix}`
    }
  )
  
  return (
    <UserProvider>
      <RouterProvider router={router}/>
      <Login/>
      <Logout/>
    </UserProvider>
  );
}

export default App;
