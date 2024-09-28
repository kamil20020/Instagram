import React, { createContext, useEffect, useRef, useState } from 'react';
import logo from './logo.svg';
import './App.css';
import { createRoutesFromElements } from "react-router";
import {
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

axios.interceptors.request.use(function (config) {

  const accessToken = localStorage.getItem("access_token")

  if(accessToken){
    config.headers.Authorization = AuthService.getAuthorizationBearerHeader(accessToken)
  }

  return config
})

function App() {

  const router = createBrowserRouter(
    createRoutesFromElements(
      <Route
        path="/"
        element={
          <ProtectedRoute>
            <Chat/>
          </ProtectedRoute>
        }
      />
    )
  )
  
  return (
    <UserProvider>
       <ProtectedRoute>
        <Chat/>
      </ProtectedRoute>
      <Login/>
    </UserProvider>
  );
}

// <div>
//   {messages.map((message: Message) => (
//     <div key={message.id}>{message.content}</div>
//   ))}
// </div>

export default App;
