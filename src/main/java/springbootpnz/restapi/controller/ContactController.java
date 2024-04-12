package springbootpnz.restapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import springbootpnz.restapi.entity.User;
import springbootpnz.restapi.model.*;
import springbootpnz.restapi.service.ContactService;

import java.util.List;

@RestController
public class ContactController {
    @Autowired
    private ContactService contactService;


    @PostMapping(
        path = "/api/contacts",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<ContactResponse> create(User user, @RequestBody CreateContactRequest request){
        ContactResponse contactResponse = contactService.createContact(user,request);

        return WebResponse.<ContactResponse>builder().data(contactResponse).build();
    };


    @GetMapping(
            path = "/api/contacts/{contactId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<ContactResponse> get(User user, @PathVariable String contactId){
        ContactResponse contactResponse = contactService.get(user, contactId);

        return WebResponse.<ContactResponse>builder().data(contactResponse).build();
    };


    @PutMapping(
            path = "/api/contacts/{contactId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<ContactResponse> update(User user,
                                               @PathVariable String contactId,
                                               @RequestBody UpdateContactRequest request){

        request.setId(contactId);
        ContactResponse contactResponse = contactService.update(user, request);

        return WebResponse.<ContactResponse>builder().data(contactResponse).build();
    };

    @DeleteMapping(
            path ="/api/contacts/{contactId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> delete(User user, @PathVariable String contactId){
        contactService.delete(user,contactId);
        return WebResponse.<String>builder().data("OK").build();
    }


    @GetMapping(
            path = "/api/contacts",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<ContactResponse>> search(User user,
                                                     @RequestParam(value = "name", required = false) String name ,
                                                     @RequestParam(value = "email", required = false) String email,
                                                     @RequestParam(value = "phone", required = false) String phone,
                                                     @RequestParam(value = "page", required = true, defaultValue = "0") Integer page,
                                                     @RequestParam(value = "size", required = true, defaultValue =  "10") Integer size){
        SearchContactRequest searchRequest = SearchContactRequest.builder()
                .page(page)
                .size(size)
                .name(name)
                .email(email)
                .phone(phone)
                .build();

       Page<ContactResponse> contactResponses =  contactService.search(user,searchRequest);
       return WebResponse.<List<ContactResponse>>builder()
               .data(contactResponses.getContent())
               .paging(PagingResponse.builder()
                       .currentPage(contactResponses.getNumber())
                       .totalPage(contactResponses.getTotalPages())
                       .size(contactResponses.getSize())
                       .build())
               .build();
    }
}
