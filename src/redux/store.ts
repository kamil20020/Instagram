import { configureStore } from "@reduxjs/toolkit";
import { useDispatch } from "react-redux";
import rootReducer from "./rootReducer";

const localStorageState = localStorage.getItem('userPreferences')
const persistedState = localStorageState
                       ? JSON.parse(localStorageState as string)
                       : {}

export const store = configureStore({
    reducer: rootReducer,
    preloadedState: persistedState,
    devTools: process.env.NODE_ENV !== 'production'
})

store.subscribe(() => {
    localStorage.setItem('userPreferences', JSON.stringify(store.getState()))
})

export type AppDispatch = typeof store.dispatch
export const useAppDispatch = () => useDispatch()