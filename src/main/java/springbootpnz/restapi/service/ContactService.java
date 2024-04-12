package springbootpnz.restapi.service;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import springbootpnz.restapi.entity.Contact;
import springbootpnz.restapi.entity.User;
import springbootpnz.restapi.model.ContactResponse;
import springbootpnz.restapi.model.CreateContactRequest;
import springbootpnz.restapi.model.SearchContactRequest;
import springbootpnz.restapi.model.UpdateContactRequest;
import springbootpnz.restapi.repository.ContactRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class ContactService {

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private ValidationService validationService;


    private ContactResponse toContactResponse(Contact contact) {
        return ContactResponse.builder()
                .id(contact.getId())
                .firstName(contact.getFirstName())
                .lastName(contact.getLastName())
                .phone(contact.getPhone())
                .email(contact.getEmail())
                .build();
    }


    @Transactional
    public ContactResponse createContact(User user, CreateContactRequest request) {
        validationService.Validate(request);

        Contact contact = new Contact();
        contact.setId(UUID.randomUUID().toString());
        contact.setUser(user);
        contact.setFirstName(request.getFirstName());
        contact.setLastName(request.getLastName());
        contact.setEmail(request.getEmail());
        contact.setPhone(request.getPhone());

        contactRepository.save(contact);

        return toContactResponse(contact);
    }

    @Transactional(readOnly = true)
    public ContactResponse get(User user, String id) {
        Contact contact = contactRepository.findFirstByUserAndId(user, id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Contact not found"));
        return toContactResponse(contact);
    }

    @Transactional
    public ContactResponse update(User user, UpdateContactRequest request) {
        validationService.Validate(request);

        Contact contact = contactRepository.findById(request.getId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Contact not found"));

        if(Objects.nonNull(request.getEmail())) {
            contact.setEmail(request.getEmail());
        }
        if(Objects.nonNull(request.getPhone())) {
            contact.setPhone(request.getPhone());
        }
        if(Objects.nonNull(request.getFirstName())) {
            contact.setFirstName(request.getFirstName());
        }
        if(Objects.nonNull(request.getLastName())) {
            contact.setLastName(request.getLastName());
        }
        contactRepository.save(contact);
        return toContactResponse(contact);
    }

    @Transactional
    public void delete(User user, String contactId) {
        Contact contact = contactRepository.findById(contactId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Contact not found"));

        contactRepository.delete(contact);

    }

    @Transactional(readOnly = true)
    public Page<ContactResponse> search(User user, SearchContactRequest request) {
        Specification<Contact> specification = (root,query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(builder.equal(root.get("user"), user));
            if (Objects.nonNull(request.getName())) {
                predicates.add(builder.or(
                        builder.like(root.get("firstName"), "%" + request.getName() + "%"),
                        builder.like(root.get("lastName"), "%" + request.getName() + "%")
                ));
            }

            if (Objects.nonNull(request.getPhone())) {
                predicates.add(builder.like(root.get("phone"), "%" + request.getPhone() + "%"));
            }

            if (Objects.nonNull(request.getEmail())) {
                predicates.add(builder.like(root.get("email"), "%" + request.getEmail() + "%"));
            }

            return query.where(predicates.toArray(new Predicate[]{})).getRestriction();
        };

        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        Page<Contact> contacts = contactRepository.findAll(specification, pageable);

        List<ContactResponse> contactResponses = contacts.getContent().stream().map(this::toContactResponse).toList();

        return new PageImpl<>(contactResponses, pageable, contacts.getTotalElements());
    }
}

