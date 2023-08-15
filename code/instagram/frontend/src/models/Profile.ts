export interface Profile {
    id: string,
    nickname: string,
    firstname: string,
    surname: string,
    avatar?: string,
    description: string,
    followers: number,
    followings: number,
    numberOfPosts: number,
    isVerified: boolean,
    isPrivate: boolean,
}
//https://img.freepik.com/darmowe-zdjecie/gory-vestrahorn-w-stokksnes-na-islandii_335224-667.jpg?w=1380&t=st=1674737578~exp=1674738178~hmac=c701828364cf1333d4ab301db868dabee2a0231cb886bd09dc709852524ae1d9