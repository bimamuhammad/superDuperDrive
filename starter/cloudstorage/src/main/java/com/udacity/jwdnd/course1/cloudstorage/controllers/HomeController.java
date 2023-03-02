package com.udacity.jwdnd.course1.cloudstorage.controllers;

import com.udacity.jwdnd.course1.cloudstorage.mapper.CredentialMapper;
import com.udacity.jwdnd.course1.cloudstorage.mapper.FilesMapper;
import com.udacity.jwdnd.course1.cloudstorage.mapper.NoteMapper;
import com.udacity.jwdnd.course1.cloudstorage.mapper.UserMapper;
import com.udacity.jwdnd.course1.cloudstorage.model.Credentials;
import com.udacity.jwdnd.course1.cloudstorage.model.Files;
import com.udacity.jwdnd.course1.cloudstorage.model.Notes;
import com.udacity.jwdnd.course1.cloudstorage.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping
public class HomeController {
    private final UserMapper userMapper;
    private final FilesMapper filesMapper;
    private final NoteMapper noteMapper;
    private final CredentialMapper credentialMapper;

    public HomeController(UserMapper userMapper, FilesMapper filesMapper, NoteMapper noteMapper, CredentialMapper credentialMapper) {
        this.userMapper = userMapper;
        this.filesMapper = filesMapper;
        this.noteMapper = noteMapper;
        this.credentialMapper = credentialMapper;

    }

    @GetMapping("/home")
    public String getHome(@ModelAttribute("credsUpload") Credentials credentials,@ModelAttribute("notesUpload") Notes notesUpload,Authentication authentication, Model model){
        User user = userMapper.getUser(authentication.getName());
        if(user == null) {
           authentication.setAuthenticated(false);
           System.out.println("Not authenticated");
        }else {
            System.out.println("Authenticated");
            List<Files> files = filesMapper.getFiles(user.getUserid());
            model.addAttribute("files", files);

            List<Notes> notes = noteMapper.getNotes(user.getUserid());
            model.addAttribute("notes", notes);

            List<Credentials> creds = credentialMapper.getCredentials(user.getUserid());
            model.addAttribute("credentials", creds);
        }

            return "home";
    }

    @PostMapping("/home")
    public String postHome(@ModelAttribute("credsUpload") Credentials credentials,@ModelAttribute("notesUpload") Notes notesUpload,@RequestParam("fileUpload") MultipartFile file, Authentication authentication, Model model) throws IOException {

        User user = userMapper.getUser(authentication.getName());
        if(user == null) {
            authentication.setAuthenticated(false);
            System.out.println("Not authenticated");
        }else {
            Files uploadedFile = new Files(null, file.getOriginalFilename(), file.getContentType(), Long.toString(file.getSize()), user.getUserid(), file.getBytes());

            this.filesMapper.addFile(uploadedFile);
            // load files for display
            List<Files> files = filesMapper.getFiles(user.getUserid());
            model.addAttribute("files", files);

            List<Notes> notes = noteMapper.getNotes(user.getUserid());
            model.addAttribute("notes", notes);

            List<Credentials> creds = credentialMapper.getCredentials(user.getUserid());
            model.addAttribute("credentials", creds);
        }
        return "home";
    }


    @GetMapping("/notes")
    public String getNotes(@ModelAttribute("credsUpload") Credentials credentials,@ModelAttribute("notesUpload") Notes notesUpload,Authentication authentication, Model model){
        User user = userMapper.getUser(authentication.getName());
        if(user == null) {
            authentication.setAuthenticated(false);
            System.out.println("Not authenticated");
        }else {
            List<Files> files = filesMapper.getFiles(user.getUserid());
            model.addAttribute("files", files);

            List<Notes> notes = noteMapper.getNotes(user.getUserid());
            model.addAttribute("notes", notes);

            List<Credentials> creds = credentialMapper.getCredentials(user.getUserid());
            model.addAttribute("credentials", creds);
        }
        return "home";
    }

    @PostMapping("/notes")
    public String postNotes(@ModelAttribute("credsUpload") Credentials credentials, @ModelAttribute("notesUpload") Notes notes, Model model, Authentication authentication){
        User user = userMapper.getUser(authentication.getName());
        if(user == null) {
            authentication.setAuthenticated(false);
            System.out.println("Not authenticated");
        }else {
            notes.setUserid(user.getUserid());
            if(notes.getNoteid() == null) {
                this.noteMapper.addNote(notes);
            }else{
                this.noteMapper.updateNotes(notes);
            }


            List<Files> files = filesMapper.getFiles(user.getUserid());
            model.addAttribute("files", files);

            List<Notes> notesList = this.noteMapper.getNotes(user.getUserid());
            model.addAttribute("notes", notesList);

            List<Credentials> creds = credentialMapper.getCredentials(user.getUserid());
            model.addAttribute("credentials", creds);
        }
            return "home";
    }

    @GetMapping("/credsUpload")
    public String getCreds(@ModelAttribute("credsUpload") Credentials credentials,@ModelAttribute("notesUpload") Notes notesUpload,Authentication authentication, Model model){
        User user = userMapper.getUser(authentication.getName());
        if(user == null) {
            authentication.setAuthenticated(false);
            System.out.println("Not authenticated");
        }else {
            if(credentials.getCredentialid() == null) {
                this.credentialMapper.createCredential(credentials);
            }else{
                this.credentialMapper.updateCredential(credentials);
            }
            List<Files> files = filesMapper.getFiles(user.getUserid());
            model.addAttribute("files", files);

            List<Notes> notes = noteMapper.getNotes(user.getUserid());
            model.addAttribute("notes", notes);

            List<Credentials> creds = credentialMapper.getCredentials(user.getUserid());
            model.addAttribute("credentials", creds);
        }
            return "home";
    }

    @PostMapping("/credsUpload")
    public String postCreds(@ModelAttribute("credsUpload") Credentials credentials, @ModelAttribute("notesUpload") Notes notes, Model model, Authentication authentication){
        User user = userMapper.getUser(authentication.getName());
        if(user == null) {
            authentication.setAuthenticated(false);
            System.out.println("Not authenticated");
        }else {
            credentials.setUserid(user.getUserid());
            this.credentialMapper.createCredential(credentials);

            List<Files> files = filesMapper.getFiles(user.getUserid());
            model.addAttribute("files", files);

            List<Notes> notesList = this.noteMapper.getNotes(user.getUserid());
            model.addAttribute("notes", notesList);

            List<Credentials> creds = credentialMapper.getCredentials(user.getUserid());
            model.addAttribute("credentials", creds);
        }
            return "home";
    }

    @GetMapping("delete")
    public String delete(@RequestParam(required = false, name = "file") Integer fileId, @RequestParam(required = false, name = "note") Integer noteId, @RequestParam(required = false, name = "creds") Integer credId, @ModelAttribute("credsUpload") Credentials credentials, @ModelAttribute("notesUpload") Notes notes, Model model, Authentication authentication){
        if(fileId!=null){
            filesMapper.removeFile(fileId);
        } else if (noteId!=null) {
            noteMapper.removeNote(noteId);
        } else if (credId != null) {
            credentialMapper.removeCredential(credId);
        }

        User user = userMapper.getUser(authentication.getName());
        if(user == null) {
            authentication.setAuthenticated(false);
            System.out.println("Not authenticated");
        }else {
            List<Files> files = filesMapper.getFiles(user.getUserid());
            model.addAttribute("files", files);

            List<Notes> notesList = noteMapper.getNotes(user.getUserid());
            model.addAttribute("notes", notesList);

            List<Credentials> creds = credentialMapper.getCredentials(user.getUserid());
            model.addAttribute("credentials", creds);
        }
        return "home";
    }
}
