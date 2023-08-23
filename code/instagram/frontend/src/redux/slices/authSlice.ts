import { createSlice, PayloadAction } from "@reduxjs/toolkit";
import { RootState } from "../rootReducer";

export interface LoggedUser {
  id: string;
  nickname: string;
  avatar?: string;
}

export interface AuthState {
  user?: LoggedUser;
  isAuthenticated: boolean;
}

const initialState: AuthState = {
  isAuthenticated: false,
  user: {
    id: "96f2e9fa-d4f0-4067-a272-04cc766f688b",
    nickname: "kamil",
  },
};

export const authSlice = createSlice({
  name: "auth",
  initialState,
  reducers: {
    login(state, action: PayloadAction<AuthState>) {
      const newState = action.payload;
      state.user = newState.user;
      state.isAuthenticated = true;
    },
    logout(state) {
      state.user = undefined;
      state.isAuthenticated = false;
    },
  },
});

export const { login, logout } = authSlice.actions;
export const useAuthSelector = (state: RootState) => state.auth;
export default authSlice.reducer;
