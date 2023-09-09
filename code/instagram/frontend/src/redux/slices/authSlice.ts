import { createSlice, PayloadAction } from "@reduxjs/toolkit";
import { RootState } from "../rootReducer";
import { BasicUserData } from "../../models/BasicUserData";

export interface AuthState {
  user?: BasicUserData;
  isAuthenticated: boolean;
}

const initialState: AuthState = {
  isAuthenticated: false
};

export const authSlice = createSlice({
  name: "auth",
  initialState,
  reducers: {
    login(state, action: PayloadAction<BasicUserData>) {
      state.user = action.payload;
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
