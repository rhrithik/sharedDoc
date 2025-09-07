package org.hrithik.documenteditor.schemas;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
public class UserSchema {
    @Id
    private String id;
    private String username;
    private String email;
    private String password;
    private String hashedPassword;
    private String role;

    public UserSchema(String username, String email, String password, String hashedPassword, String role){
        this.username = username;
        this.email = email;
        this.password = password;
        this.hashedPassword = hashedPassword;
        this.role = role;
    }
}
