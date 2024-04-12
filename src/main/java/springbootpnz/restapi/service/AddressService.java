package springbootpnz.restapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import springbootpnz.restapi.entity.Address;
import springbootpnz.restapi.entity.Contact;
import springbootpnz.restapi.entity.User;
import springbootpnz.restapi.model.AddressResponse;
import springbootpnz.restapi.model.CreateAddressRequest;
import springbootpnz.restapi.model.UpdateAddressRequest;
import springbootpnz.restapi.repository.AddressRepository;
import springbootpnz.restapi.repository.ContactRepository;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class AddressService {
    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private ValidationService validationService;


    private AddressResponse toAddressResponse(Address address) {
        return AddressResponse.builder()
                .id(address.getId())
                .city(address.getCity())
                .country(address.getCountry())
                .street(address.getStreet())
                .postalCode(address.getPostalCode())
                .province(address.getProvince())
                .build();
    };

    @Transactional
    public AddressResponse create(User user, CreateAddressRequest request) {
        validationService.Validate(request);

        Contact contact = contactRepository.findFirstByUserAndId(user, request.getContactId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found"));

        Address address = new Address();
        address.setId(UUID.randomUUID().toString());
        address.setContact(contact);
        address.setCity(request.getCity());
        address.setCountry(request.getCountry());
        address.setStreet(request.getStreet());
        address.setPostalCode(request.getPostalCode());
        address.setProvince(request.getProvince());

        addressRepository.save(address);

        return toAddressResponse(address);
    }

    @Transactional
    public AddressResponse update(User user, UpdateAddressRequest request) {
        validationService.Validate(request);

        Contact contact = contactRepository.findFirstByUserAndId(user, request.getContactId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found"));

        Address address = addressRepository.findFirstByContactAndId(contact, request.getAddressId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Address not found"));

        if (Objects.nonNull(request.getCity())) {
            address.setCity(request.getCity());
        };

        if (Objects.nonNull(request.getCountry())) {
            address.setCountry(request.getCountry());
        };

        if (Objects.nonNull(request.getStreet())) {
            address.setStreet(request.getStreet());
        };

        if (Objects.nonNull(request.getPostalCode())) {
            address.setPostalCode(request.getPostalCode());
        };

        if (Objects.nonNull(request.getProvince())) {
            address.setProvince(request.getProvince());
        };
        addressRepository.save(address);
        return toAddressResponse(address);
    };

    @Transactional(readOnly = true)
    public AddressResponse get(User user,String contactId, String addressId) {

        Contact contact = contactRepository.findFirstByUserAndId(user, contactId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found"));
        Address address = addressRepository.findFirstByContactAndId(contact, addressId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Address not found"));

        return toAddressResponse(address);
    }

    @Transactional(readOnly = true)
    public List<AddressResponse> getByContact(User user, String contactId) {
        Contact contact = contactRepository.findFirstByUserAndId(user, contactId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found"));
        List<Address> addresses = addressRepository.findAllByContact(contact);

        return addresses.stream().map(this::toAddressResponse).toList();
    }

    @Transactional
    public void delete(User user, String contactId, String addressId) {
        Contact contact = contactRepository.findFirstByUserAndId(user, contactId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found"));
        Address address = addressRepository.findFirstByContactAndId(contact, addressId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Address not found"));
        addressRepository.delete(address);
    }
}
