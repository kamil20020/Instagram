import { createSlice, PayloadAction } from "@reduxjs/toolkit";
import { RootState } from "../rootReducer";
import { UserHeader } from "../../models/responses/UserHeader";

export interface AuthState {
  user?: UserHeader;
  isAuthenticated: boolean;
}

const initialState: AuthState = {
  isAuthenticated: false,
};

export const authSlice = createSlice({
  name: "auth",
  initialState,
  reducers: {
    login(state, action: PayloadAction<UserHeader>) {
      state.user = action.payload;
      state.isAuthenticated = true;
    },
    updateUserData(state, action: PayloadAction<UserHeader>) {
      state.user = action.payload
    },
    logout(state) {
      state.user = undefined;
      state.isAuthenticated = false;
    },
  },
});

export const { login, updateUserData, logout } = authSlice.actions;
export const useAuthSelector = (state: RootState) => state.auth;
export default authSlice.reducer;
