package com.udacity.jwdnd.course1.cloudstorage;

import com.udacity.jwdnd.course1.cloudstorage.pageobjects.HomePage;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import java.io.File;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CloudStorageApplicationTests {

	private HomePage homePage;

	@LocalServerPort
	private int port;

	private WebDriver driver;

	@BeforeAll
	static void beforeAll() {
		WebDriverManager.chromedriver().setup();
	}

	@BeforeEach
	public void beforeEach() {
		this.driver = new ChromeDriver();
	}

	@AfterEach
	public void afterEach() {
		if (this.driver != null) {
			driver.quit();
		}
	}

	@Test
	public void getLoginPage() {
		driver.get("http://localhost:" + this.port + "/login");
		Assertions.assertEquals("Login", driver.getTitle());
	}

	/**
	 * PLEASE DO NOT DELETE THIS method.
	 * Helper method for Udacity-supplied sanity checks.
	 **/
	private void doMockSignUp(String firstName, String lastName, String userName, String password){
		// Create a dummy account for logging in later.

		// Visit the sign-up page.
		WebDriverWait webDriverWait = new WebDriverWait(driver, 10);
		driver.get("http://localhost:" + this.port + "/signup");
		webDriverWait.until(ExpectedConditions.titleContains("Sign Up"));
		
		// Fill out credentials
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("inputFirstName")));
		WebElement inputFirstName = driver.findElement(By.id("inputFirstName"));
		inputFirstName.click();
		inputFirstName.sendKeys(firstName);

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("inputLastName")));
		WebElement inputLastName = driver.findElement(By.id("inputLastName"));
		inputLastName.click();
		inputLastName.sendKeys(lastName);

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("inputUsername")));
		WebElement inputUsername = driver.findElement(By.id("inputUsername"));
		inputUsername.click();
		inputUsername.sendKeys(userName);

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("inputPassword")));
		WebElement inputPassword = driver.findElement(By.id("inputPassword"));
		inputPassword.click();
		inputPassword.sendKeys(password);

		// Attempt to sign up.
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("buttonSignUp")));
		WebElement buttonSignUp = driver.findElement(By.id("buttonSignUp"));
		buttonSignUp.click();

		/* Check that the sign up was successful. 
		// You may have to modify the element "success-msg" and the sign-up 
		// success message below depening on the rest of your code.
		*/
		Assertions.assertTrue(driver.findElement(By.id("success-msg")).getText().contains("You successfully signed up!"));
	}

	
	
	/**
	 * PLEASE DO NOT DELETE THIS method.
	 * Helper method for Udacity-supplied sanity checks.
	 **/
	private void doLogIn(String userName, String password)
	{
		// Log in to our dummy account.
		driver.get("http://localhost:" + this.port + "/login");
		WebDriverWait webDriverWait = new WebDriverWait(driver, 2);

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("inputUsername")));
		WebElement loginUserName = driver.findElement(By.id("inputUsername"));
		loginUserName.click();
		loginUserName.sendKeys(userName);

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("inputPassword")));
		WebElement loginPassword = driver.findElement(By.id("inputPassword"));
		loginPassword.click();
		loginPassword.sendKeys(password);

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("login-button")));
		WebElement loginButton = driver.findElement(By.id("login-button"));
		loginButton.click();

		webDriverWait.until(ExpectedConditions.titleContains("Home"));

	}

	/**
	 * PLEASE DO NOT DELETE THIS TEST. You may modify this test to work with the 
	 * rest of your code. 
	 * This test is provided by Udacity to perform some basic sanity testing of 
	 * your code to ensure that it meets certain rubric criteria. 
	 * 
	 * If this test is failing, please ensure that you are handling redirecting users 
	 * back to the login page after a succesful sign up.
	 * Read more about the requirement in the rubric: 
	 * https://review.udacity.com/#!/rubrics/2724/view 
	 */
	@Test
	public void testRedirection() {
		// Create a test account
		doMockSignUp("Redirection","Test","RT","123");
		
		// Check if we have been redirected to the log in page.
		Assertions.assertEquals("http://localhost:" + this.port + "/login", driver.getCurrentUrl());
	}

	/**
	 * PLEASE DO NOT DELETE THIS TEST. You may modify this test to work with the 
	 * rest of your code. 
	 * This test is provided by Udacity to perform some basic sanity testing of 
	 * your code to ensure that it meets certain rubric criteria. 
	 * 
	 * If this test is failing, please ensure that you are handling bad URLs 
	 * gracefully, for example with a custom error page.
	 * 
	 * Read more about custom error pages at: 
	 * https://attacomsian.com/blog/spring-boot-custom-error-page#displaying-custom-error-page
	 */
	@Test
	public void testBadUrl() {
		// Create a test account
		doMockSignUp("URL","Test","UT","123");
		doLogIn("UT", "123");
		
		// Try to access a random made-up URL.
		driver.get("http://localhost:" + this.port + "/some-random-page");
		Assertions.assertFalse(driver.getPageSource().contains("Whitelabel Error Page"));
	}


	/**
	 * PLEASE DO NOT DELETE THIS TEST. You may modify this test to work with the 
	 * rest of your code. 
	 * This test is provided by Udacity to perform some basic sanity testing of 
	 * your code to ensure that it meets certain rubric criteria. 
	 * 
	 * If this test is failing, please ensure that you are handling uploading large files (>1MB),
	 * gracefully in your code. 
	 * 
	 * Read more about file size limits here: 
	 * https://spring.io/guides/gs/uploading-files/ under the "Tuning File Upload Limits" section.
	 */
	@Test
	public void testLargeUpload() {
		// Create a test account
		doMockSignUp("Large File","Test","LFT","123");
		doLogIn("LFT", "123");

		// Try to upload an arbitrary large file
		WebDriverWait webDriverWait = new WebDriverWait(driver, 2);
		String fileName = "upload5m.zip";

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("fileUpload")));
		WebElement fileSelectButton = driver.findElement(By.id("fileUpload"));
		fileSelectButton.sendKeys(new File(fileName).getAbsolutePath());

		WebElement uploadButton = driver.findElement(By.id("uploadButton"));
		uploadButton.click();
		try {
			webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.id("success")));
		} catch (org.openqa.selenium.TimeoutException e) {
			System.out.println("Large File upload failed");
		}
		Assertions.assertFalse(driver.getPageSource().contains("HTTP Status 403 – Forbidden"));

	}

	/**
	 * Unauthorized users can access signup and login pages only
	 */
	@Test
	public void testUnauthUsers(){
		WebDriverWait webDriverWait = new WebDriverWait(driver, 2);
		driver.get("http://localhost:" + this.port + "/login");

		Assertions.assertEquals("http://localhost:" + this.port + "/login", driver.getCurrentUrl());

		driver.get("http://localhost:" + this.port + "/signup");
		Assertions.assertEquals("http://localhost:" + this.port + "/signup", driver.getCurrentUrl());

		driver.get("http://localhost:" + this.port + "/home");
		Assertions.assertNotEquals("http://localhost:" + this.port + "/home", driver.getCurrentUrl());
		Assertions.assertEquals("http://localhost:" + this.port + "/login", driver.getCurrentUrl());
	}

	@Test
	public void testSignUpLoginLogout(){
		doMockSignUp("bima", "Sima", "bensima", "password");

		doLogIn("bensima", "password");
		Assertions.assertEquals("http://localhost:" + this.port + "/home", driver.getCurrentUrl());

		WebDriverWait webDriverWait = new WebDriverWait(driver, 2);
		WebElement logoutElement = driver.findElement(By.id("logout"));
		logoutElement.click();
		webDriverWait.until(ExpectedConditions.urlContains("login-logout"));
		driver.get("http://localhost:" + this.port + "/home");
		Assertions.assertNotEquals("http://localhost:" + this.port + "/home", driver.getCurrentUrl());
		Assertions.assertEquals("http://localhost:" + this.port + "/login", driver.getCurrentUrl());

	}

	/**
	 * Write Tests for Note Creation, Viewing, Editing, and Deletion.
	 */
	@Test
	public void testNoteFuntion(){
		doMockSignUp("bima", "Sima", "bensimaa", "password");
		doLogIn("bensima", "password");
		driver.get("http://localhost:" + this.port + "/home");
		homePage = new HomePage(driver);

		// Open notes tab
		WebDriverWait wait = new WebDriverWait(driver, 20);
		homePage.openTab("notes");
		wait.until(ExpectedConditions.elementToBeClickable(By.id("addNote")));
		homePage.openNotes();

		// Add new note
		wait.until(ExpectedConditions.elementToBeClickable(By.id("note-title")));
		homePage.addNotes("first", "I love this");
		homePage.openTab("notes");
		wait.until(ExpectedConditions.presenceOfElementLocated(By.id("rowfirst")));
		WebElement row = driver.findElement(By.id("rowfirst"));

		wait.until(ExpectedConditions.textToBePresentInElement(row,"I love this" ));
		Assertions.assertTrue(driver.findElement(By.id("rowfirst")).getText().contains("I love this"));

		// Edit Existing
		wait.until(ExpectedConditions.elementToBeClickable(By.id("editfirst"))).click();
		wait.until(ExpectedConditions.elementToBeClickable(By.id("note-title")));
		homePage.addNotes("", " Now it has been edited");
		homePage.openTab("notes");
		wait.until(ExpectedConditions.presenceOfElementLocated(By.id("rowfirst")));
		row = driver.findElement(By.id("rowfirst"));

		wait.until(ExpectedConditions.textToBePresentInElement(row,"first" ));
		Assertions.assertTrue(driver.findElement(By.id("rowfirst")).getText().contains("I love this Now it has been edited"));

		// Delete  note
		homePage.openTab("notes");
		wait.until(ExpectedConditions.elementToBeClickable(By.id("delfirst"))).click();
		WebElement del = null;
		try{
			del = driver.findElement(By.id("rowfirst"));
		}catch (Exception exception){
			System.out.print("Element s null");
		}
		Assertions.assertNull(del);

	}

	/**
	 * Write Tests for Credential Creation, Viewing, Editing, and Deletion
	 */
	@Test
	public void testCredentials(){
		doMockSignUp("bima", "Sima", "biaa", "password");
		doLogIn("biaa", "password");
		driver.get("http://localhost:" + this.port + "/home");
		homePage = new HomePage(driver);

		homePage.openTab("creds");
		WebDriverWait wait = new WebDriverWait(driver, 20);
		wait.until(ExpectedConditions.elementToBeClickable(By.id("addCred"))).click();

		wait.until(ExpectedConditions.elementToBeClickable(By.id("credential-url")));
		homePage.setAddCredential("google.com", "ilham", "tg111234");
		homePage.openTab("creds");
		;

		wait.until(ExpectedConditions.textToBePresentInElement(
				wait.until(ExpectedConditions.presenceOfElementLocated(By.id("credgoogle.com"))),
				"google.com" ));
		Assertions.assertTrue(driver.findElement(By.id("credgoogle.com")).getText().contains("google.com"));
		Assertions.assertEquals(driver.findElement(By.id("credgoogle.comusername")).getText(), "ilham");
		Assertions.assertNotEquals(driver.findElement(By.id("credgoogle.comencpwd")).getText(), "tg111234");

		// View saved credential
		wait.until(ExpectedConditions.elementToBeClickable(By.id("editcredgoogle.com"))).click();
		wait.until(ExpectedConditions.elementToBeClickable(By.id("credential-password")));
		Assertions.assertEquals(homePage.getCredentialPassword().getAttribute("value"), "tg111234");

		// Edit the credential
		homePage.setAddCredential("google.com", "sahib", "tg122234");
		homePage.openTab("creds");
		// Verify password has changed
		wait.until(ExpectedConditions.elementToBeClickable(By.id("editcredgoogle.com"))).click();
		wait.until(ExpectedConditions.elementToBeClickable(By.id("credential-password")));
		Assertions.assertNotEquals(homePage.getCredentialPassword().getAttribute("value"), "tg111234");
		Assertions.assertEquals(homePage.getCredentialPassword().getAttribute("value"), "tg122234");

		// Delete Credential
		WebElement closeButton = driver.findElement(By.id("close-btn"));
		closeButton.click();
		wait.until(ExpectedConditions.elementToBeClickable(By.id("delcredgoogle.com"))).click();
		WebElement del = null;
		try{
			del = driver.findElement(By.id("credgoogle.com"));
		}catch (Exception exception){
			System.out.print("Element s null");
		}
		Assertions.assertNull(del);
	}

}
