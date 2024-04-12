package springbootpnz.restapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import springbootpnz.restapi.entity.User;
import springbootpnz.restapi.model.AddressResponse;
import springbootpnz.restapi.model.CreateAddressRequest;
import springbootpnz.restapi.model.UpdateAddressRequest;
import springbootpnz.restapi.model.WebResponse;
import springbootpnz.restapi.service.AddressService;

import java.util.List;

@RestController
public class AddressController {
    @Autowired
    private AddressService addressService;

    @PostMapping(
            path = "/api/contacts/{contactId}/addresses",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<AddressResponse> create(User user,
                                               @PathVariable(value = "contactId") String contactId,
                                               @RequestBody CreateAddressRequest request) {
        request.setContactId(contactId);
        AddressResponse addressResponse = addressService.create(user, request);

        return WebResponse.<AddressResponse>builder().data(addressResponse).build();
    };

    @PutMapping(
            path = "/api/contacts/{contactId}/addresses/{addressId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<AddressResponse> update(User user,
                                               @PathVariable(value = "contactId") String contactId,
                                               @PathVariable(value = "addressId") String addressId,
                                               @RequestBody UpdateAddressRequest request) {
        request.setContactId(contactId);
        request.setAddressId(addressId);
        AddressResponse addressResponse = addressService.update(user, request);
        return WebResponse.<AddressResponse>builder()
                        .data(addressResponse)
                        .build();
    }


    @GetMapping(
            path = "/api/contacts/{contactId}/addresses/{addressId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<AddressResponse> get(User user,
                                            @PathVariable(value = "contactId") String contactId,
                                            @PathVariable(value = "addressId") String addressId) {
        AddressResponse addressResponse = addressService.get(user,contactId,addressId);
        return WebResponse.<AddressResponse>builder().data(addressResponse).build();
    };



    @GetMapping(
            path = "/api/contacts/{contactId}/addresses",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<AddressResponse>> getAllByContact(User user,
                                            @PathVariable(value = "contactId") String contactId) {
        List<AddressResponse> addresses = addressService.getByContact(user,contactId);
        return WebResponse.<List<AddressResponse>>builder().data(addresses).build();
    };

    @DeleteMapping(
            path = "/api/contacts/{contactId}/addresses/{addressId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> delete(User user,
                                      @PathVariable(value = "contactId") String contactId,
                                      @PathVariable(value = "addressId") String addressId){
        addressService.delete(user, contactId, addressId);
        return WebResponse.<String>builder().data("OK").build();
    }

}
