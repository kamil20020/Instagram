import { BasicUserData } from "./BasicUserData";

export interface Post {
    id: string,
    img: string,
    description: string,
    areDisabledComments: boolean,
    areHiddenLikes: boolean,
    userData: BasicUserData,
    numberOfLikes: number,
    numberOfComments: number
}