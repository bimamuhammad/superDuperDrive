package com.udacity.jwdnd.course1.cloudstorage.pageobjects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class HomePage {
    public HomePage(WebDriver driver){
        PageFactory.initElements(driver, this);
    }


    // NoteElements
    @FindBy(id="nav-notes-tab")
    private WebElement noteTab;

    @FindBy(id="addNote")
    private WebElement addTab;

    @FindBy(id="note-title")
    private WebElement noteTitle;

    @FindBy(id="note-description")
    private WebElement noteDescription;

    @FindBy(id = "submitNote")
    private WebElement submitNoteBtn;

    // Credentials
    @FindBy(id="nav-credentials-tab")
    private WebElement credTab;

    @FindBy(id="addCred")
    private WebElement addCredential;

    @FindBy(id = "credential-id")
    private WebElement credentialId;

    @FindBy(id="credential-url")
    private WebElement credentialUrl;

    @FindBy(id="credential-username")
    private WebElement credentiaUsername;

    @FindBy(id="credential-password")
    private WebElement credentialPassword;

    @FindBy(id="submit-btn")
    private WebElement submitCredential;

    public void setAddCredential(String url, String username, String password){
        this.credentialUrl.clear();
        this.credentialUrl.sendKeys(url);
        this.credentiaUsername.sendKeys(username);
        this.credentialPassword.clear();
        this.credentialPassword.sendKeys(password);
        this.submitCredential.click();
    }

    // Functions
    public void openTab(String type){
        if (type.equalsIgnoreCase("notes")) {
            noteTab.click();
        } else if (type.equalsIgnoreCase("creds")) {
            credTab.click();
        }
    }

    public void openNotes(){
        this.addTab.click();
    }
    public void addNotes(String title, String description){

        this.noteTitle.sendKeys(title);

        this.noteDescription.sendKeys(description);

        this.submitNoteBtn.click();
    }

    public WebElement getCredentialPassword() {
        return credentialPassword;
    }
}
