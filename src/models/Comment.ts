import { BasicUserData } from "./BasicUserData";

export interface Comment {
    id: string,
    userData: BasicUserData,
    content: string,
    creationDatetime: string
}