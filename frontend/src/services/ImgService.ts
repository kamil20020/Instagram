class ImgService {

  fixBase64(base64: string){
    return base64.split(',')[1]
  }
}

export default new ImgService()