import { combineReducers } from '@reduxjs/toolkit'
import userPreferencesReducer from './slices/userPreferencesSlice'
import notificationReducer from './slices/notificationSlice'
import authReducer from './slices/authSlice'
import commentReducer from './slices/commentSlice'

const rootReducer = combineReducers({
    userPreferences: userPreferencesReducer,
    notification: notificationReducer,
    auth: authReducer,
    comment: commentReducer
})

export type RootState = ReturnType<typeof rootReducer>

export default rootReducer