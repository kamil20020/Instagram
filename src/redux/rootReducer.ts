import { combineReducers } from '@reduxjs/toolkit'
import userPreferencesReducer from './slices/userPreferencesSlice'
import notificationReducer from './slices/notificationSlice'
import authReducer from './slices/authSlice'

const rootReducer = combineReducers({
    userPreferences: userPreferencesReducer,
    notification: notificationReducer,
    auth: authReducer
})

export type RootState = ReturnType<typeof rootReducer>

export default rootReducer