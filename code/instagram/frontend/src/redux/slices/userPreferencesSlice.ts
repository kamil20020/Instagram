import { createSlice, PayloadAction } from "@reduxjs/toolkit";
import { Profile } from "../../models/Profile";
import { RootState } from "../rootReducer";

export interface UserPreferencesState {
  latestProfilesIds: string[];
  isCreatingComment: boolean
}

export const initialState: UserPreferencesState = {
  latestProfilesIds: [],
  isCreatingComment: false
};

export const userPreferencesSlice = createSlice({
  name: "userPreferences",
  initialState,
  reducers: {
    setLatestProfiles(state, action: PayloadAction<string[]>) {
      state.latestProfilesIds = action.payload
    },
    clearLatestProfiles(state){
      state.latestProfilesIds = []
    },
    switchIsCreatingComment(state){
      state.isCreatingComment = !state.isCreatingComment
    }
  },
});

export const { setLatestProfiles, clearLatestProfiles, switchIsCreatingComment } = userPreferencesSlice.actions
export const userPreferencesSelector = (state: RootState) => state.userPreferences
export default userPreferencesSlice.reducer