import { createSlice, PayloadAction } from "@reduxjs/toolkit"
import { RootState } from "../rootReducer";

export enum NotificationType {
    success="success",
    error="error"
}

export interface NotificationState {
    message: string,
    type: NotificationType,
    isVisible: boolean
}

export interface CreateNotification {
    message: string,
    type: NotificationType
}

const initialState: NotificationState = {
    message: "",
    type: NotificationType.success,
    isVisible: false
}

export const notificationSlice = createSlice({
    name: "notification",
    initialState,
    reducers: {
        setNotification(state, action: PayloadAction<CreateNotification>){
            const newState = action.payload
            state.message = newState.message
            state.type = newState.type
            state.isVisible = true
        },
        close(state){
            state.isVisible = false
        }
    }
})

export const {setNotification, close} = notificationSlice.actions
export const useNotificationSelector = (state: RootState) => state.notification
export default notificationSlice.reducer