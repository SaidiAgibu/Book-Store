package com.saidi.book_store.dto;

import com.saidi.book_store.models.Role;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {
    private Long userId;
    private String firstName;
    private String lastName;
    @Email
    private String email;
    private boolean enabled;
    private Collection<Role> roles = new ArrayList<>();
}
