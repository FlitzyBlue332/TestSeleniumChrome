package org.flitzyblue332;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

public class TestChrome {
    private WebDriver driver;
    private WebDriverWait wait;
    private final String EMAIL = "arturiagiallo332@gmail.com";
    private final String PASSWORD = "Char245Cello";

    @BeforeClass
    public void setup() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--incognito");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        driver.get("https://mail.google.com");
        handleLogin();
    }

    @Test
    public void testGmailDelete(){
        WebElement lastUnreadEmail = findLastUnreadEmail();
        String lastThreadID = getEmailID(lastUnreadEmail);
        Reporter.log("Subject: " + getEmailSubject(lastUnreadEmail));
        System.out.println("Subject: " + lastThreadID);
        deleteEmail(lastUnreadEmail);
//        verifyEmailDeletion(lastThreadID);
        navigateToTrash();
        verifyEmailDeletedInTrash(lastThreadID);
    }

    @Test
    public void testGmailDeleteCheckInbox(){
        driver.get("https://mail.google.com");
        WebElement lastUnreadEmail = findLastUnreadEmail();
        String lastThreadID = getEmailID(lastUnreadEmail);
        Reporter.log("Subject: " + getEmailSubject(lastUnreadEmail));
        System.out.println("Subject: " + lastThreadID);
        deleteEmail(lastUnreadEmail);
        verifyEmailDeletion(lastThreadID);
//        navigateToTrash();
//        verifyEmailDeletedInTrash(lastThreadID);
    }


    private void handleLogin(){
        // handle login
        // email
        WebElement emailInput = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("input[type='email']")));
        emailInput.sendKeys(EMAIL);
        driver.findElement(By.id("identifierNext")).click();

        // password
        WebElement passwordInput = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("input[type='password']")));
        passwordInput.sendKeys(PASSWORD);
        driver.findElement(By.id("passwordNext")).click();

        // handle otp
        List<WebElement> otpElements = driver.findElements(By.cssSelector("div[data-challenge='TOTP']"));
        if(!otpElements.isEmpty()) {
            // found otp -> manual input, no other choice
            System.out.println("Manual OTP input required - waiting 20 seconds");
            wait.withTimeout(Duration.ofSeconds(20)).until(ExpectedConditions.invisibilityOfElementLocated(
                    By.cssSelector("div[data-challenge='TOTP']")));
        }
    }

    private WebElement findLastUnreadEmail(){
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("div[role='main']")));
        List<WebElement> unreadEmails = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                By.cssSelector("tr.zA.zE:not(.bqO)")));
//        List<WebElement> unreadEmails = driver.findElements(By.cssSelector("tr.zA.zE:not(.bqO)"));
        return unreadEmails.getFirst();
    }

    private String getEmailSubject(WebElement emailElement){
        WebElement subjectElement = emailElement.findElement(By.cssSelector("span.bog"));
        return subjectElement.getText().trim();
    }

    private String getEmailID(WebElement emailElement){
        return emailElement.findElement(By.cssSelector(".bqe")).getDomAttribute("data-thread-id");
    }

    private void deleteEmail(WebElement emailElement){
        emailElement.findElement(By.cssSelector("div.oZ-jc")).click();
        wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("div[aria-label^='Delete']"))).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("span.bAq")));
//        wait.until(ExpectedConditions.stalenessOf(emailElement));
    }

    private void verifyEmailDeletion(String deletedEmailThreadID) {
//        driver.navigate().refresh();
        List<WebElement> allEmails = driver.findElements(
                By.cssSelector("tr.zA.zE:not(.bqO)"));

        Boolean emailExists = allEmails.stream().anyMatch(email ->
                Objects.equals(getEmailID(email), deletedEmailThreadID));
        Assert.assertFalse(emailExists, "Supposedly deleted email still exist");
    }

    private void navigateToTrash() {
        driver.get("https://mail.google.com/mail/u/0/#trash");
    }

    private void verifyEmailDeletedInTrash(String deletedEmailThreadID) {
//        driver.navigate().refresh();
//        List<WebElement> allTrashEmails = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
//                By.cssSelector("div[role='main'] tr.zA.zE")));
        wait.until(ExpectedConditions.titleContains("Trash"));
        List<WebElement> allTrashEmails = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                By.cssSelector("div[role='main'] tr.zA.zE")));

        Boolean emailExists = allTrashEmails.stream().anyMatch(email ->
                Objects.equals(getEmailID(email), deletedEmailThreadID));

        Assert.assertTrue(emailExists, "Supposedly deleted email found in trash");

    }

//    @AfterClass
//    public void tearDown() {
//        if (driver != null) {
//            try {
//                Thread.sleep(5000);
//            } catch (Exception e){
//
//            }
//            driver.quit();
//        }
//    }

}
