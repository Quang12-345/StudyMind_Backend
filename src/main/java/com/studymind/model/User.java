package com.studymind.model;

import com.studymind.model.enums.UserRole;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "users")
public class User extends BaseDocument {

    @Indexed(unique = true)
    private String email;

    private String passwordHash;
    private String fullName;
    private String avatarUrl;
    private UserRole role = UserRole.STUDENT;
}
