package pl.instagram.instagram.model.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PersonalData(

    @NotBlank(message = "Imię jest wymagane")
    String firstname,

    @NotBlank(message = "Nazwisko jest wymagane")
    String surname,

    @Size(min = 5, max = 255, message = "Rozmiar pseudonimu powinien być od 5 do 255")
    @NotBlank(message = "Pseudonim jest wymagany")
    String nickname
){}
