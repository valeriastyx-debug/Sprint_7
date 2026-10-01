package models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Courier {

    private String login;
    private String password;
    private String firstName;
}
