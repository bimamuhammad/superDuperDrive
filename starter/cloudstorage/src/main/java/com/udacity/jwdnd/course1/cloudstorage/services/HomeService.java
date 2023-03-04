package com.udacity.jwdnd.course1.cloudstorage.services;

import com.udacity.jwdnd.course1.cloudstorage.mapper.FilesMapper;
import com.udacity.jwdnd.course1.cloudstorage.model.CredentialForm;
import com.udacity.jwdnd.course1.cloudstorage.model.Credentials;
import com.udacity.jwdnd.course1.cloudstorage.model.Files;
import org.apache.catalina.webresources.FileResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;

@Service
public class HomeService {

    private EncryptionService encryptionService;
    private FilesMapper filesMapper;

    public HomeService(EncryptionService encryptionService, FilesMapper filesMapper) {
        this.encryptionService = encryptionService;
        this.filesMapper = filesMapper;
    }

    public List<CredentialForm> decryptCredPwd(List<Credentials> credentialsList){
        List<CredentialForm> credentialFormList = new ArrayList<CredentialForm>();
        Iterator credentialIter= credentialsList.iterator();
        while(credentialIter.hasNext()){
            Credentials creds = (Credentials) credentialIter.next();
            String plainPass = encryptionService.decryptValue(creds.getPassword(), creds.getKey());
            credentialFormList.add(new CredentialForm(creds.getCredentialid(), creds.getUrl(), creds.getUsername(), creds.getKey(), creds.getPassword(), creds.getUserid(), plainPass));
        }
        return credentialFormList;
    }

    public boolean fileExists(String filename, Integer userId){
        List<Files> fileList = filesMapper.CheckFile(filename, userId);
        return fileList.size()>0;
    }

    public Files downloadFile(Integer fileid, Integer userId){
        List<Files> filelist = filesMapper.getFile(fileid, userId);
        return filelist.get(0);
    }

    public Credentials createCredential(CredentialForm credentialForm, Integer userid){
        SecureRandom random = new SecureRandom();
        byte[] key = new byte[16];
        random.nextBytes(key);
        String encodedKey = Base64.getEncoder().encodeToString(key);
        String encryptedPassword = encryptionService.encryptValue(credentialForm.getPlainPassword(), encodedKey);
        return new Credentials(credentialForm.getCredentialid(), credentialForm.getUrl(), credentialForm.getUsername(), encodedKey, encryptedPassword, userid);
    }

//    public FileResource uploadFile(MultipartFile file) throws IOException {
//        InputStream in = file.getInputStream();
//        FileOutputStream f = new FileOutputStream(file.getOriginalFilename());
//        FileResource fs = new FileResource(new File(file.getOriginalFilename()))''
//    }
}
