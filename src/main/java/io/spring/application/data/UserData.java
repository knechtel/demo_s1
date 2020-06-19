package io.spring.application.data;

import lombok.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserData {
    @Getter @Setter private String id;
    @Getter @Setter  private String email;
    @Getter @Setter private String username;
    @Getter @Setter private String bio;
    @Getter @Setter private String image;
}
