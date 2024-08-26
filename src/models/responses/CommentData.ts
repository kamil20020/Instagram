import { UserHeader } from "./UserHeader";

export interface CommentData {
    id: string,
    author: UserHeader,
    content: string,
    creationDatetime: string,
    subCommentsCount: number,
    likesCount: number
}