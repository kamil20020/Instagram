import { BasicUserData } from "./BasicUserData";

export interface Post {
    id: string,
    img: string,
    description: string,
    areDisabledComments: boolean,
    creationDatetime: string,
    areHiddenLikes: boolean,
    userData: BasicUserData,
    numberOfLikes: number,
    numberOfComments: number
}