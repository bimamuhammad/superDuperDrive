package com.udacity.jwdnd.course1.cloudstorage.controllers;

import com.udacity.jwdnd.course1.cloudstorage.mapper.CredentialMapper;
import com.udacity.jwdnd.course1.cloudstorage.mapper.FilesMapper;
import com.udacity.jwdnd.course1.cloudstorage.mapper.NoteMapper;
import com.udacity.jwdnd.course1.cloudstorage.mapper.UserMapper;
import com.udacity.jwdnd.course1.cloudstorage.model.*;
import com.udacity.jwdnd.course1.cloudstorage.services.EncryptionService;
import com.udacity.jwdnd.course1.cloudstorage.services.HashService;
import com.udacity.jwdnd.course1.cloudstorage.services.HomeService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;

@Controller
@RequestMapping
public class HomeController {
    private final UserMapper userMapper;
    private final FilesMapper filesMapper;
    private final NoteMapper noteMapper;
    private final CredentialMapper credentialMapper;
    private final EncryptionService encryptionService;
    private  final HomeService homeService;

    public HomeController(UserMapper userMapper, FilesMapper filesMapper, NoteMapper noteMapper, CredentialMapper credentialMapper, EncryptionService encryptionService, HomeService homeService) {
        this.userMapper = userMapper;
        this.filesMapper = filesMapper;
        this.noteMapper = noteMapper;
        this.credentialMapper = credentialMapper;
        this.encryptionService = encryptionService;
        this.homeService = homeService;

    }

    @GetMapping("/files")
    @ResponseBody
    public ResponseEntity<Resource> downloadFile(@RequestParam(required = false, name = "file") Integer fileId, Authentication authentication){
        User user = userMapper.getUser(authentication.getName());
        if(user == null) {
            authentication.setAuthenticated(false);
            System.out.println("Not authenticated");
        }else {
            Files files = homeService.downloadFile(fileId, user.getUserid());
            ByteArrayResource resource = new ByteArrayResource(files.getFiledata());
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""+files.getFilename()+"\"").body(resource);
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ByteArrayResource("HTTP Status 403 – Forbidden".getBytes()));
    }
    @GetMapping("/home")
    public String getHome(@ModelAttribute("credsUpload") CredentialForm credentials, @ModelAttribute("notesUpload") Notes notesUpload, Authentication authentication, Model model){
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

            List<CredentialForm> credsList = this.homeService.decryptCredPwd(credentialMapper.getCredentials(user.getUserid()));
            model.addAttribute("credentials", credsList);
        }

            return "home";
    }

    @PostMapping("/home")
    public String postHome(@ModelAttribute("credsUpload") CredentialForm credentials,@ModelAttribute("notesUpload") Notes notesUpload,@RequestParam("fileUpload") MultipartFile file, Authentication authentication, Model model) throws IOException {

        User user = userMapper.getUser(authentication.getName());
        if(user == null) {
            authentication.setAuthenticated(false);
            System.out.println("Not authenticated");
        }else {

            if(!this.homeService.fileExists(file.getOriginalFilename(), user.getUserid()) && file.getSize()>0) {
                Files uploadedFile = new Files(null, file.getOriginalFilename(), file.getContentType(), Long.toString(file.getSize()), user.getUserid(), file.getBytes());
                this.filesMapper.addFile(uploadedFile);
                model.addAttribute("signpsuccess", "File upload successful");
            }else{
                model.addAttribute("fileuploadMessage", "File already exists or is null");
            }
            // load files for display
            List<Files> files = filesMapper.getFiles(user.getUserid());
            model.addAttribute("files", files);

            List<Notes> notes = noteMapper.getNotes(user.getUserid());
            model.addAttribute("notes", notes);

            List<CredentialForm> credsList = this.homeService.decryptCredPwd(credentialMapper.getCredentials(user.getUserid()));
            model.addAttribute("credentials", credsList);
        }
        return "home";
    }


    @GetMapping("/notes")
    public String getNotes(@ModelAttribute("credsUpload") CredentialForm credentials,@ModelAttribute("notesUpload") Notes notesUpload,Authentication authentication, Model model){
        User user = userMapper.getUser(authentication.getName());
        if(user == null) {
            authentication.setAuthenticated(false);
            System.out.println("Not authenticated");
        }else {
            List<Files> files = filesMapper.getFiles(user.getUserid());
            model.addAttribute("files", files);

            List<Notes> notes = noteMapper.getNotes(user.getUserid());
            model.addAttribute("notes", notes);

            List<CredentialForm> credsList = this.homeService.decryptCredPwd(credentialMapper.getCredentials(user.getUserid()));
            model.addAttribute("credentials", credsList);
        }
        return "home";
    }

    @PostMapping("/notes")
    public String postNotes(@ModelAttribute("credsUpload") CredentialForm credentials, @ModelAttribute("notesUpload") Notes notes, Model model, Authentication authentication){
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

            List<CredentialForm> credsList = this.homeService.decryptCredPwd(credentialMapper.getCredentials(user.getUserid()));
            model.addAttribute("credentials", credsList);
        }
            return "home";
    }

    @GetMapping("/credsUpload")
    public String getCreds(@ModelAttribute("credsUpload") CredentialForm credentials,@ModelAttribute("notesUpload") Notes notesUpload,Authentication authentication, Model model){
        User user = userMapper.getUser(authentication.getName());
        if(user == null) {
            authentication.setAuthenticated(false);
            System.out.println("Not authenticated");
        }else {
            
            List<Files> files = filesMapper.getFiles(user.getUserid());
            model.addAttribute("files", files);

            List<Notes> notes = noteMapper.getNotes(user.getUserid());
            model.addAttribute("notes", notes);

            List<CredentialForm> credsList = this.homeService.decryptCredPwd(credentialMapper.getCredentials(user.getUserid()));
            model.addAttribute("credentials", credsList);
        }
            return "home";
    }

    @PostMapping("/credsUpload")
    public String postCreds(@ModelAttribute("credsUpload") CredentialForm credentials, @ModelAttribute("notesUpload") Notes notes, Model model, Authentication authentication){
        User user = userMapper.getUser(authentication.getName());
        if(user == null) {
            authentication.setAuthenticated(false);
            System.out.println("Not authenticated");
        }else {
            Credentials creds = homeService.createCredential(credentials, user.getUserid());
            if(credentials.getCredentialid() == null) {
                this.credentialMapper.createCredential(creds);
            }else{
                this.credentialMapper.updateCredential(creds);
            }

            List<Files> files = filesMapper.getFiles(user.getUserid());
            model.addAttribute("files", files);

            List<Notes> notesList = this.noteMapper.getNotes(user.getUserid());
            model.addAttribute("notes", notesList);

            List<CredentialForm> credsList = this.homeService.decryptCredPwd(credentialMapper.getCredentials(user.getUserid()));
            model.addAttribute("credentials", credsList);
        }
            return "home";
    }

    @GetMapping("/delete")
    public String delete(@RequestParam(required = false, name = "file") Integer fileId, @RequestParam(required = false, name = "note") Integer noteId, @RequestParam(required = false, name = "creds") Integer credId, @ModelAttribute("credsUpload") CredentialForm credentials, @ModelAttribute("notesUpload") Notes notes, Model model, Authentication authentication){

        User user = userMapper.getUser(authentication.getName());
        if(user == null) {
            authentication.setAuthenticated(false);
            System.out.println("Not authenticated");
        }else {
            if(fileId!=null){
                filesMapper.removeFile(fileId);
            } else if (noteId!=null) {
                noteMapper.removeNote(noteId);
            } else if (credId != null) {
                credentialMapper.removeCredential(credId);
            }

            List<Files> files = filesMapper.getFiles(user.getUserid());
            model.addAttribute("files", files);

            List<Notes> notesList = noteMapper.getNotes(user.getUserid());
            model.addAttribute("notes", notesList);

            List<CredentialForm> credsList = this.homeService.decryptCredPwd(credentialMapper.getCredentials(user.getUserid()));
            model.addAttribute("credentials", credsList);
        }
        return "home";
    }
}
