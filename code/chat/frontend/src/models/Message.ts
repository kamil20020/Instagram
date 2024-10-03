export interface Message {
    id: string,
    senderAccountId: string,
    receiverAccountId: string,
    content: string,
    creationDate: string,
    read: boolean
}