package pl.instagram.instagram.model.api.request;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateComment {

    private String content;
}
