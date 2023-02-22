import { createSlice, PayloadAction } from "@reduxjs/toolkit";
import { Profile } from "../../models/Profile";
import { RootState } from "../rootReducer";

export interface UserPreferencesState {
  latestProfilesIds: number[];
}

export const initialState: UserPreferencesState = {
  latestProfilesIds: [],
};

export const userPreferencesSlice = createSlice({
  name: "userPreferences",
  initialState,
  reducers: {
    setLatestProfiles(state, action: PayloadAction<number[]>) {
      state.latestProfilesIds = action.payload
    },
    clearLatestProfiles(state){
      state.latestProfilesIds = []
    }
  },
});

export const { setLatestProfiles, clearLatestProfiles } = userPreferencesSlice.actions
export const userPreferencesSelector = (state: RootState) => state.userPreferences
export default userPreferencesSlice.reducer