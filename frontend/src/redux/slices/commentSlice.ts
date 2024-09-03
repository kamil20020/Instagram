import { createSlice, PayloadAction } from "@reduxjs/toolkit";
import { RootState } from "../rootReducer";

export interface CommentState {
  isCreating: boolean;
  content: string;
  parentCommentId?: string;
}

export const initialState: CommentState = {
  isCreating: false,
  content: ""
};

export const commentSlice = createSlice({
  name: "comment",
  initialState,
  reducers: {
    focusComment(state, action: PayloadAction<string | undefined>){
      state.content = ""
      state.isCreating = true
      state.parentCommentId = action.payload
    },
    switchIsCreatingComment(state){
      state.isCreating = !state.isCreating
    },
    setCommentContent(state, action: PayloadAction<string>){
      state.content = action.payload
    },
    setParentCommentId(state, action: PayloadAction<string | undefined>){
      state.parentCommentId = action.payload
    },
    finishComment(state){
      state.isCreating = false
    },
    clearComment(state){
        state.content = ""
        state.isCreating = false
        state.parentCommentId = undefined
    }
  },
});

export const { switchIsCreatingComment, setCommentContent, setParentCommentId, clearComment, focusComment, finishComment } = commentSlice.actions
export const commentSelector = (state: RootState) => state.comment
export default commentSlice.reducer