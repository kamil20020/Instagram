import React from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import App from './App';
import reportWebVitals from './reportWebVitals';
import { Auth0Provider } from '@auth0/auth0-react';

const root = ReactDOM.createRoot(
  document.getElementById('root') as HTMLElement
);

const auth0Domain = process.env.REACT_APP_AUTH0_DOMAIN as string
const auth0ClientId = process.env.REACT_APP_AUTH0_CLIENT_ID as string
const auth0Audience = process.env.REACT_APP_AUTH0_AUDIENCE
const auth0Scope = process.env.REACT_APP_AUTH0_SCOPE

root.render(
  <Auth0Provider
    domain={auth0Domain}
    clientId={auth0ClientId}
    authorizationParams={{
      audience: auth0Audience,
      scope: auth0Scope,
      redirect_uri: window.location.origin
    }}
  >
      <App />
  </Auth0Provider>
);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();
