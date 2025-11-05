package org.hrithik.documenteditor.schemas;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
public class UserSchema {
    @Id
    private String id;
    private String username;
    private String password; // will be encoded

    public UserSchema() {}

    public UserSchema(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
}
