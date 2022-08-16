package com.smartcontactmanager.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.sql.Time;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
 
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import com.smartcontactmanager.dao.ContactRepository;
import com.smartcontactmanager.dao.UserRepository;
import com.smartcontactmanager.entities.Contact;
import com.smartcontactmanager.entities.User;
import com.smartcontactmanager.helper.Message;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContactRepository contactRepository;

    @ModelAttribute
    public void addCommonData(Model model, Principal principal) {

        String userName = principal.getName();
        System.out.println(userName);

        User user = userRepository.getUserByUserName(userName);

        model.addAttribute("user", user);

    }

    @RequestMapping("/dashboard")
    public String index(Model model, Principal principal) {

        return "normal/dashboard";
    }

    // open add form handler

    @GetMapping("/add-contact")
    public String openAddContactFrom(Model model) {
        model.addAttribute("title", "Add Contact");
        model.addAttribute("contact", new Contact());
        return "normal/add_contact_form";
    }

    // processing ad contact form
    @PostMapping("/process-contact")
    public String processContact(@ModelAttribute Contact contact,
            @RequestParam("profileImage") MultipartFile multipartFile, Principal principal,HttpSession session) {
        try {
            User user = userRepository.getUserByUserName(principal.getName());
            user.getContacts().add(contact);
            contact.setUser(user);
            if (!multipartFile.isEmpty()) {
                File file = new ClassPathResource("/static/img").getFile();
                String fileName = System.currentTimeMillis() + multipartFile.getOriginalFilename();
                Path filePath = Paths.get(file.getAbsolutePath() + File.separator + fileName);

                Files.copy(multipartFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                contact.setImage(fileName);
            }

            userRepository.save(user);

            System.out.println(contact);
            session.setAttribute("message", new Message("Contact has been added successfully!!!", "alert-success"));


        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("message", new Message("An error occured pls try again!!!", "alert-danger"));
        }

        return "normal/add_contact_form";
    }

    //show all contacts
    @GetMapping("/show-contacts/{page}")
    public String showContacts(@PathVariable("page")int page, Model model,Principal principal)
    {
        model.addAttribute("title", "Contacts");
        User user=this.userRepository.getUserByUserName(principal.getName());

        //current-page
        //total no of contacts per page
         Pageable pageable = PageRequest.of(page, 5);

       Page<Contact> contacts = this.contactRepository.findContactsByUser(user.getId(),pageable);

       model.addAttribute("contacts", contacts);
       model.addAttribute("currentPage", page);
       model.addAttribute("totalPages", contacts.getTotalPages());

        return "normal/show_contacts";
    }

    //show single contact
    @GetMapping(value="/{cId}/contact")
    public String showContactById(@PathVariable("cId")int cId,Model model)
    {
        Optional<Contact> cOptional = this.contactRepository.findById(cId);
        Contact contact=cOptional.get();
        model.addAttribute("contact",contact);
        model.addAttribute("title",contact.getName());
        return "normal/one_contact";

    }

}
