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
import springbootpnz.restapi.entity.Contact;
import springbootpnz.restapi.entity.User;
import springbootpnz.restapi.model.ContactResponse;
import springbootpnz.restapi.model.CreateContactRequest;
import springbootpnz.restapi.model.UpdateContactRequest;
import springbootpnz.restapi.model.WebResponse;
import springbootpnz.restapi.repository.ContactRepository;
import springbootpnz.restapi.repository.UserRepository;
import springbootpnz.restapi.security.BCrypt;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ContactControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        contactRepository.deleteAll();
        userRepository.deleteAll();


        User user = new User();
        user.setUsername("johndoe");
        user.setName("John Doe");
        user.setPassword(BCrypt.hashpw("password", BCrypt.gensalt()));
        user.setToken("testUser");
        user.setTokenExpiredAt(System.currentTimeMillis() + 10000000L);
        userRepository.save(user);
    }



    @Test
    void testCreateBadRequest() throws Exception {
        CreateContactRequest request = new CreateContactRequest();

        request.setFirstName("");
        request.setLastName("Doe");
        request.setEmail("john.doe@mail.com");
        request.setPhone("1122334455");

        mockMvc.perform(
                    post("/api/contacts")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .accept(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(request))
                            .header("X-API-TOKEN","testUser")
                ).andExpectAll(
                        status().isBadRequest()
                ).andDo(result -> {
                    WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>() {});
                    assertNotNull(response.getErrors());

        });
    }

    @Test
    void testCreateSuccess() throws Exception {
        CreateContactRequest request = new CreateContactRequest();

        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("john.doe@mail.com");
        request.setPhone("1122334455");

        mockMvc.perform(
                post("/api/contacts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-API-TOKEN","testUser")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<ContactResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<ContactResponse>>() {});
            assertNull(response.getErrors());

            assertNotNull(response.getData());
            assertEquals("John", response.getData().getFirstName());
            assertEquals("Doe", response.getData().getLastName());
            assertEquals("john.doe@mail.com", response.getData().getEmail());
            assertEquals("1122334455", response.getData().getPhone());

            assertTrue(contactRepository.existsById(response.getData().getId()));
        });
    }

    @Test
    void testGetNotFound() throws Exception {
        mockMvc.perform(
                get("/api/contacts/12341232")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("X-API-TOKEN","testUser")
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>() {});
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void testGetSuccess() throws Exception {
        User user = userRepository.findById("johndoe").orElse(null);

        Contact contact = new Contact();
        contact.setId(UUID.randomUUID().toString());
        contact.setFirstName("John");
        contact.setLastName("Doe");
        contact.setEmail("john.doe@mail.com");
        contact.setPhone("1122334455");
        contact.setUser(user);

        contactRepository.save(contact);

        mockMvc.perform(
                get("/api/contacts/"+contact.getId())
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .header("X-API-TOKEN","testUser")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<ContactResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<ContactResponse>>() {});
            assertNull(response.getErrors());

            assertEquals(contact.getId(), response.getData().getId());
            assertEquals(contact.getFirstName(), response.getData().getFirstName());
            assertEquals(contact.getLastName(), response.getData().getLastName());
            assertEquals(contact.getEmail(), response.getData().getEmail());
            assertEquals(contact.getPhone(), response.getData().getPhone());
            assertTrue(contactRepository.existsById(response.getData().getId()));
        });
    }

    @Test
    void testUpdateSuccess() throws Exception {
        User user = userRepository.findById("johndoe").orElse(null);

        Contact contact = new Contact();
        contact.setId(UUID.randomUUID().toString());
        contact.setFirstName("John");
        contact.setLastName("Doe");
        contact.setEmail("john.doe@mail.com");
        contact.setPhone("1122334455");
        contact.setUser(user);
        contactRepository.save(contact);

        UpdateContactRequest request = new UpdateContactRequest();
        request.setFirstName("Updated John");
        request.setLastName("Doe");
        request.setPhone("1122334455");
        mockMvc.perform(
                put("/api/contacts/"+contact.getId())
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("X-API-TOKEN","testUser")
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
           WebResponse<ContactResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<ContactResponse>>() {});
           assertNull(response.getErrors());

           assertEquals(contact.getId(), response.getData().getId());
           assertEquals(request.getFirstName(), response.getData().getFirstName());
           assertEquals(request.getLastName(), response.getData().getLastName());
           assertEquals(request.getPhone(), response.getData().getPhone());

           assertTrue(contactRepository.existsById(response.getData().getId()));

           assertEquals(contact.getEmail(), response.getData().getEmail());
        });
    }

    @Test
    void testUpdateBadRequest() throws Exception {

        UpdateContactRequest request = new UpdateContactRequest();

        request.setFirstName("");
        request.setLastName("Doe");

        mockMvc.perform(
                put("/api/contacts/12345")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-API-TOKEN","testUser")
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>() {});
            assertNotNull(response.getErrors());

        });

    }


    @Test
    void testUpdateNotFound() throws Exception {
        User user = userRepository.findById("johndoe").orElse(null);

        Contact contact = new Contact();
        contact.setId(UUID.randomUUID().toString());
        contact.setFirstName("John");
        contact.setLastName("Doe");
        contact.setEmail("john.doe@mail.com");
        contact.setPhone("1122334455");
        contact.setUser(user);
        contactRepository.save(contact);

        UpdateContactRequest request = new UpdateContactRequest();
        request.setFirstName("Updated John");
        request.setLastName("Doe");
        request.setPhone("1122334455");
        mockMvc.perform(
                put("/api/contacts/"+contact.getId() +"1234")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("X-API-TOKEN","testUser")
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>() {});
            assertNotNull(response.getErrors());
        });
    }



    @Test
    void testUpdateUnauthorized() throws Exception {
        User user = userRepository.findById("johndoe").orElse(null);

        Contact contact = new Contact();
        contact.setId(UUID.randomUUID().toString());
        contact.setFirstName("John");
        contact.setLastName("Doe");
        contact.setEmail("john.doe@mail.com");
        contact.setPhone("1122334455");
        contact.setUser(user);
        contactRepository.save(contact);


        User userNew = new User();
        userNew.setName("Elon Musk");
        userNew.setUsername("elonMusk");
        userNew.setPassword(BCrypt.hashpw("elonMusk", BCrypt.gensalt()));
        userNew.setToken("spaceX");
        userNew.setTokenExpiredAt(System.currentTimeMillis()+ 1000000000L);


        UpdateContactRequest request = new UpdateContactRequest();
        request.setFirstName("Updated John");
        request.setLastName("Doe");
        request.setPhone("1122334455");
        mockMvc.perform(
                put("/api/contacts/"+contact.getId())
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("X-API-TOKEN","spaceX")
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>() {});
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void testDeleteNotFound() throws Exception {
        User user = userRepository.findById("johndoe").orElse(null);
        Contact contact = new Contact();

        contact.setId(UUID.randomUUID().toString());
        contact.setUser(user);
        contact.setFirstName("John");
        contact.setLastName("Doe");
        contact.setEmail("john.doe@mail.com");
        contact.setPhone("1122334455");


        contactRepository.save(contact);

        mockMvc.perform(
                delete("/api/contacts/" + contact.getId() +"1234")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .header("X-API-TOKEN","testUser")
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>(){});

            assertNotNull(response.getErrors());

            Contact contactAfter = contactRepository.findById(contact.getId()).orElse(null);

            assertNotNull(contactAfter);

        });
    }

    @Test
    void testDeleteSuccess() throws Exception {
        User user = userRepository.findById("johndoe").orElse(null);
        Contact contact = new Contact();

        contact.setId(UUID.randomUUID().toString());
        contact.setUser(user);
        contact.setFirstName("John");
        contact.setLastName("Doe");
        contact.setEmail("john.doe@mail.com");
        contact.setPhone("1122334455");


        contactRepository.save(contact);


        mockMvc.perform(
                delete("/api/contacts/" + contact.getId())
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .header("X-API-TOKEN","testUser")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>(){});

            assertNull(response.getErrors());

            Contact contactAfter = contactRepository.findById(contact.getId()).orElse(null);

            assertNull(contactAfter);

        });
    }

    @Test
    void testSearchNotFound() throws Exception {
        mockMvc.perform(
                get("/api/contacts")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .header("X-API-TOKEN","testUser")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<List<ContactResponse>> contactResponses = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<List<ContactResponse>>>() {});

            assertNull(contactResponses.getErrors());

            assertEquals(0, contactResponses.getData().size());
            assertEquals(0, contactResponses.getPaging().getTotalPage());
            assertEquals(0, contactResponses.getPaging().getCurrentPage());
            assertEquals(10, contactResponses.getPaging().getSize());
        });
    }



    @Test
    void testSearchUsingName() throws Exception {

        User user = userRepository.findById("johndoe").orElse(null);

        for (int i = 0; i < 100; i++) {

            Contact contact = new Contact();

            contact.setId(UUID.randomUUID().toString());
            contact.setUser(user);
            contact.setFirstName("John " + i);
            contact.setLastName("Doe");
            contact.setEmail("john.doe@mail.com");
            contact.setPhone("1122334455");

            contactRepository.save(contact);
        }

        mockMvc.perform(
                get("/api/contacts")
                        .queryParam("name","John")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .header("X-API-TOKEN","testUser")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<List<ContactResponse>> contactResponses = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<List<ContactResponse>>>() {});

            assertNull(contactResponses.getErrors());

            assertEquals(10, contactResponses.getData().size());
            assertEquals(10, contactResponses.getPaging().getTotalPage());
            assertEquals(0, contactResponses.getPaging().getCurrentPage());
            assertEquals(10, contactResponses.getPaging().getSize());
        });


        mockMvc.perform(
                get("/api/contacts")
                        .queryParam("name","Doe")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .header("X-API-TOKEN","testUser")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<List<ContactResponse>> contactResponses = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<List<ContactResponse>>>() {});

            assertNull(contactResponses.getErrors());

            assertEquals(10, contactResponses.getData().size());
            assertEquals(10, contactResponses.getPaging().getTotalPage());
            assertEquals(0, contactResponses.getPaging().getCurrentPage());
            assertEquals(10, contactResponses.getPaging().getSize());
        });



        mockMvc.perform(
                get("/api/contacts")
                        .queryParam("email","john.")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .header("X-API-TOKEN","testUser")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<List<ContactResponse>> contactResponses = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<List<ContactResponse>>>() {});

            assertNull(contactResponses.getErrors());

            assertEquals(10, contactResponses.getData().size());
            assertEquals(10, contactResponses.getPaging().getTotalPage());
            assertEquals(0, contactResponses.getPaging().getCurrentPage());
            assertEquals(10, contactResponses.getPaging().getSize());
        });




        mockMvc.perform(
                get("/api/contacts")
                        .queryParam("phone","1122")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .header("X-API-TOKEN","testUser")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<List<ContactResponse>> contactResponses = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<List<ContactResponse>>>() {});

            assertNull(contactResponses.getErrors());

            assertEquals(10, contactResponses.getData().size());
            assertEquals(10, contactResponses.getPaging().getTotalPage());
            assertEquals(0, contactResponses.getPaging().getCurrentPage());
            assertEquals(10, contactResponses.getPaging().getSize());
        });
    }

}