package com.example.warehouse.domain.documents;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Document(value = "users")
public class UserDocument implements Serializable {

    private String id;
    private String username;
    private String password;
    private String email;
    private Set<String> housesId;

}
