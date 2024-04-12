package springbootpnz.restapi.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import springbootpnz.restapi.entity.Address;
import springbootpnz.restapi.entity.Contact;
import springbootpnz.restapi.entity.User;
import springbootpnz.restapi.model.AddressResponse;
import springbootpnz.restapi.model.CreateAddressRequest;
import springbootpnz.restapi.model.UpdateAddressRequest;
import springbootpnz.restapi.model.WebResponse;
import springbootpnz.restapi.repository.AddressRepository;
import springbootpnz.restapi.repository.ContactRepository;
import springbootpnz.restapi.repository.UserRepository;
import springbootpnz.restapi.security.BCrypt;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
class AddressControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private AddressRepository addressRepository;

    @BeforeEach
    void setUp() {
        addressRepository.deleteAll();
        contactRepository.deleteAll();
        userRepository.deleteAll();

        User user = new User();
        user.setUsername("johndoe");
        user.setName("John Doe");
        user.setPassword(BCrypt.hashpw("password", BCrypt.gensalt()));
        user.setToken("testUser");
        user.setTokenExpiredAt(System.currentTimeMillis() + 10000000L);
        userRepository.save(user);

        Contact contact = new Contact();
        contact.setId("testContact");
        contact.setFirstName("test1");
        contact.setFirstName("Laura");
        contact.setLastName("Brook");
        contact.setEmail("luarabrook@gmail.com");
        contact.setUser(user);
        contact.setPhone("123456789");
        contactRepository.save(contact);

        Address address = new Address();

        address.setId("testAddresses");
        address.setCity("Bogor");
        address.setCountry("Indonesia");
        address.setStreet("Kota Bogor Jl Baranangsiang");
        address.setPostalCode("123456789");
        address.setProvince("West Java");
        address.setContact(contact);

        addressRepository.save(address);
    }

    @Test
    void testCreateAddressBadRequest() throws Exception {
        CreateAddressRequest request = new CreateAddressRequest();
        request.setCity("Bogor");
        request.setStreet("West Java");

        mockMvc.perform(
                post("/api/contacts/testContact/addresses")
                        .header("X-API-TOKEN", "testUser")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpect(
                status().isBadRequest()
        ).andDo(result -> {
                WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>() {});

                assertNotNull(response.getErrors());
        });
    }

    @Test
    void testCreateAddressSuccess() throws Exception {
        CreateAddressRequest request = new CreateAddressRequest();
        request.setCity("Bogor");
        request.setCountry("Indonesia");
        request.setStreet("Kota Bogor Jl Baranangsiang");
        request.setPostalCode("123456789");
        request.setProvince("West Java");


        mockMvc.perform(
                post("/api/contacts/testContact/addresses")
                        .header("X-API-TOKEN", "testUser")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpect(
                status().isOk()
        ).andDo(result -> {
            WebResponse<AddressResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<AddressResponse>>() {});

            assertNull(response.getErrors());

            assertTrue(addressRepository.existsById(response.getData().getId()));
            assertEquals(response.getData().getCity(), request.getCity());
            assertEquals(response.getData().getProvince(), request.getProvince());
            assertEquals(response.getData().getPostalCode(), request.getPostalCode());
            assertEquals(response.getData().getStreet(), request.getStreet());
            assertEquals(response.getData().getCountry(), request.getCountry());

        });
    }



    @Test
    void testUpdateAddressBadRequest() throws Exception {
        UpdateAddressRequest request = new UpdateAddressRequest();
        request.setCity("Bogor");
        request.setStreet("West Java");

        mockMvc.perform(
                put("/api/contacts/testContact/addresses/testAddresses")
                        .header("X-API-TOKEN", "testUser")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpect(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>() {});

            assertNotNull(response.getErrors());
        });
    }

    @Test
    void testUpdateAddressSuccess() throws Exception {

        UpdateAddressRequest request = new UpdateAddressRequest();
        request.setCity("Bogor");
        request.setCountry("Indonesia");
        request.setStreet("Kota Bogor Jl Baranangsiang");
        request.setPostalCode("123456789");
        request.setProvince("West Java");


        mockMvc.perform(
                put("/api/contacts/testContact/addresses/testAddresses")
                        .header("X-API-TOKEN", "testUser")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpect(
                status().isOk()
        ).andDo(result -> {
            WebResponse<AddressResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<AddressResponse>>() {});

            assertNull(response.getErrors());

            assertTrue(addressRepository.existsById(response.getData().getId()));
            assertEquals(response.getData().getCity(), request.getCity());
            assertEquals(response.getData().getProvince(), request.getProvince());
            assertEquals(response.getData().getPostalCode(), request.getPostalCode());
            assertEquals(response.getData().getStreet(), request.getStreet());
            assertEquals(response.getData().getCountry(), request.getCountry());

        });
    }

    @Test
    void testGetAddressNotFound() throws Exception {
        Contact contact = contactRepository.findById("testContact").orElse(null);

        Address address = new Address();

        address.setId("testAddresses");
        address.setCity("Bogor");
        address.setCountry("Indonesia");
        address.setStreet("Kota Bogor Jl Baranangsiang");
        address.setPostalCode("123456789");
        address.setProvince("West Java");
        address.setContact(contact);

        addressRepository.save(address);

        mockMvc.perform(
                get("/api/contacts/testContact/addresses/testAddresses" + "1234" )
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .header("X-API-TOKEN", "testUser")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>(){});
            assertNotNull(response.getErrors());
        });
    }


    @Test
    void testGetAddressContactNotFound() throws Exception {
        Contact contact = contactRepository.findById("testContact").orElse(null);

        Address address = new Address();

        address.setId("testAddresses");
        address.setCity("Bogor");
        address.setCountry("Indonesia");
        address.setStreet("Kota Bogor Jl Baranangsiang");
        address.setPostalCode("123456789");
        address.setProvince("West Java");
        address.setContact(contact);

        addressRepository.save(address);

        mockMvc.perform(
                get("/api/contacts/testContact1/addresses/testAddresses")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .header("X-API-TOKEN", "testUser")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>(){});
            assertNotNull(response.getErrors());
        });
    }


    @Test
    void testGetAddressSuccess() throws Exception {

        Contact contact = contactRepository.findById("testContact").orElse(null);

        Address address = new Address();

        address.setId("testAddresses");
        address.setCity("Bogor");
        address.setCountry("Indonesia");
        address.setStreet("Kota Bogor Jl Baranangsiang");
        address.setPostalCode("123456789");
        address.setProvince("West Java");
        address.setContact(contact);

        addressRepository.save(address);

        mockMvc.perform(
                get("/api/contacts/testContact/addresses/testAddresses")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .header("X-API-TOKEN", "testUser")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<AddressResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<AddressResponse>>() {});
            assertNull(response.getErrors());

            Address addressR = addressRepository.findById(address.getId()).orElse(null);

            assertNotNull(addressR);

            assertEquals(response.getData().getId(), addressR.getId());
            assertEquals(response.getData().getCountry(), addressR.getCountry());
            assertEquals(response.getData().getCity(), addressR.getCity());
            assertEquals(response.getData().getStreet(), addressR.getStreet());
            assertEquals(response.getData().getProvince(), addressR.getProvince());
            assertEquals(response.getData().getPostalCode(), addressR.getPostalCode());

        });
    }


    @Test
    void testDeleteAddressNotFound() throws Exception {
        mockMvc.perform(
                delete("/api/contacts/testContact/addresses/testAddresses" + "1234" )
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .header("X-API-TOKEN", "testUser")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>(){});
            assertNotNull(response.getErrors());
        });
    }



    @Test
    void testDeleteAddressSuccess() throws Exception {

        mockMvc.perform(
                delete("/api/contacts/testContact/addresses/testAddresses")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .header("X-API-TOKEN", "testUser")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>() {});
            assertNull(response.getErrors());

            Address addressR = addressRepository.findById("testAddresses").orElse(null);

            assertNull(addressR);

        });
    }


    @Test
    void testGetListAddressNotFound() throws Exception {
        mockMvc.perform(
                get("/api/contacts/salahContact/addresses" )
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .header("X-API-TOKEN", "testUser")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>(){});
            assertNotNull(response.getErrors());
        });
    }



    @Test
    void testGetListAddressSuccess() throws Exception {
        Contact contact = contactRepository.findById("testContact").orElse(null);

        for (int i = 0; i < 100; i++) {

            Address address = new Address();

            address.setId("testAddresses-"+ i );
            address.setCity("Bogor");
            address.setCountry("Indonesia");
            address.setStreet("Kota Bogor Jl Baranangsiang");
            address.setPostalCode("123456789");
            address.setProvince("West Java");
            address.setContact(contact);

            addressRepository.save(address);
        }
        mockMvc.perform(
                get("/api/contacts/testContact/addresses")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .header("X-API-TOKEN", "testUser")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<List<AddressResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(response.getErrors());

            assertEquals(101, response.getData().size());

        });
    }
}