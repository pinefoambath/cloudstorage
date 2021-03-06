package com.udacity.jwdnd.course1.cloudstorage.controller;

import com.udacity.jwdnd.course1.cloudstorage.model.*;
import com.udacity.jwdnd.course1.cloudstorage.services.UserService;
import com.udacity.jwdnd.course1.cloudstorage.services.*;
import com.udacity.jwdnd.course1.cloudstorage.mapper.*;
import javax.servlet.http.HttpSession;

import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
// telling it that this controller oversees everything in the /home folder
@RequestMapping("/home")
public class HomeController {

    private final UserService userService;
    private final NoteService noteService;
    private final FileService fileService;
    private final CredentialService credentialService;
    private UserMapper userMapper;
    private AuthenticationService authenticationService;
    private EncryptionService encryptionService;

    public HomeController(UserService userService, NoteService noteService, FileService fileService, CredentialService credentialService, UserMapper userMapper, AuthenticationService authenticationService, EncryptionService encryptionService) {
        this.userService = userService;
        this.noteService = noteService;
        this.fileService = fileService;
        this.credentialService = credentialService;
        this.userMapper = userMapper;
        this.authenticationService = authenticationService;
        this.encryptionService = encryptionService;
    }

//    it is good practice to use GetMapping rather than RequestMapping in getHomePage
//    also good practice to name attributes with lower case letters

    @GetMapping
    // need to tell it that some methods in here throw exceptions
    public String getHomePage(Authentication authentication, Model model) throws Exception {
        String username = authentication.getName();
        User user = userMapper.getUser(username);
        List<File> files = fileService.getAllFiles(user.getUserId());
        List<Note> notes = noteService.getAllNotes(user.getUserId());
        List<Credential> credentials = credentialService.getCredentialsByUserId(user.getUserId());
        // as we will be using the contents on the home html page we need to use the addAttribute method:
        model.addAttribute("fileForm", new FileForm());
        model.addAttribute("files", files);
        model.addAttribute("noteForm", new NoteForm());
        model.addAttribute("notes", notes);
        model.addAttribute("credentialForm", new CredentialForm());
        model.addAttribute("credentials", credentials);
        model.addAttribute("encryptionService",encryptionService);
        return "home";
    }

    @GetMapping(
            value = "/get-file/{fileName}",
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE
    )
    public @ResponseBody
    byte[] getFile(@PathVariable String fileName) {
        return fileService.getFile(fileName).getFileData();
    }

    @GetMapping("/result")
    public String showResult(
            Authentication authentication,
            @RequestParam(required = false, name = "isSuccess") Boolean isSuccess,
            @RequestParam(required = false, name = "errorType") Integer errorType,
            Model model
    ) {

        Map<String, Object> data = new HashMap<>();

        data.put("isSuccess", isSuccess);
        data.put("errorType", errorType);

        model.addAllAttributes(data);

        return "result";
    }
}
