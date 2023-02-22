import { createSlice, PayloadAction } from "@reduxjs/toolkit"
import { RootState } from "../rootReducer";

export enum NotificationType {
    success="success",
    error="error"
}

export interface NotificationState {
    message: string,
    type: NotificationType
}

const initialState: NotificationState = {
    message: "",
    type: NotificationType.success
}

export const notificationSlice = createSlice({
    name: "notification",
    initialState,
    reducers: {
        setNotification(state, action: PayloadAction<NotificationState>){
            const newState = action.payload
            state.message = newState.message
            state.type =newState.type
        }
    }
})

export const {setNotification} = notificationSlice.actions
export const useNotificationSelector = (state: RootState) => state.notification
export default notificationSlice.reducer