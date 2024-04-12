package springbootpnz.restapi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateContactRequest {

    @JsonIgnore
    @NotBlank
    private String id;

    @Nullable
    @Size(min = 3, max = 1000)
    private String firstName;

    @Nullable
    @Size(min = 3, max = 100)
    private String lastName;

    @Nullable
    @Size(min = 6, max = 100)
    private String phone;

    @Nullable
    @Email
    @Size(min = 5, max = 100)
    private String email;
}
