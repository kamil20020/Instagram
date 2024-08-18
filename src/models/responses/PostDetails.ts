import { UserHeader } from "./UserHeader";

export interface Post {
    id: string,
    content: string,
    description: string,
    areDisabledComments: boolean,
    creationDatetime: string,
    areHiddenLikes: boolean,
    author: UserHeader,
    numberOfLikes: number,
    numberOfComments: number
}