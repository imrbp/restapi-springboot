package springbootpnz.restapi.model;


import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateContactRequest {

    @NotBlank
    private String firstName;


    @Nullable
    @Size(min = 3, max = 100)
    private String lastName;


    @Size(min = 6, max = 100)
    private String phone;

    @Nullable
    @Email
    @Size(min = 5, max = 100)
    private String email;
}
