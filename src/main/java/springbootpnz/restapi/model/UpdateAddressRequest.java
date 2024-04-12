package springbootpnz.restapi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.annotation.Nullable;
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
public class UpdateAddressRequest {
    @JsonIgnore
    @NotBlank
    private String addressId;

    @JsonIgnore
    @NotBlank
    private String contactId;

    @Nullable
    @Size(min = 2, max = 200)
    private String street;

    @Nullable
    @Size(min = 2, max = 100)
    private String city;

    @Nullable
    @Size(min = 2, max = 100)
    private String province;

    @NotBlank
    @Size(min = 2, max = 100)
    private String country;

    @Nullable
    @Size(min = 2, max = 10)
    private String postalCode;
}
