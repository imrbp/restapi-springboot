package springbootpnz.restapi.model;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateUserRequest {

    @Nullable
    @Size(min = 2, max = 100)
    private String name;


    @Nullable
    @Size(min = 2, max = 100)
    private String password;
}
