import { combineReducers } from '@reduxjs/toolkit'
import userPreferencesReducer from './slices/userPreferencesSlice'
import notificationReducer from './slices/notificationSlice'

const rootReducer = combineReducers({
    userPreferences: userPreferencesReducer,
    notification: notificationReducer
})

export type RootState = ReturnType<typeof rootReducer>

export default rootReducer