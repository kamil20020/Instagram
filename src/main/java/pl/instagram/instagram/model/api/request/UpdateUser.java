package pl.instagram.instagram.model.api.request;

public record UpdateUser (
    String nickname,
    String firstname,
    String surname,
    String avatar
){}
