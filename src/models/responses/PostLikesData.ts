import { Page } from "./Page";

export interface PostLikesData {
    postId: string;
    postLikes: Page,
    didLoggedUserLikePost: boolean;
}