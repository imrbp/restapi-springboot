package springbootpnz.restapi.model;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchContactRequest {
    @Nullable
    private String name;

    @Nullable
    private String email;

    @Nullable
    private String phone;

    @NotNull
    private Integer page;

    @NotNull
    private Integer size;
}
