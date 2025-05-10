package org.flitzyblue332;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;

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
    }

    @Test
    public void testGmailDelete(){
        driver.get("https://mail.google.com");
        handleLogin();


        WebElement lastUnreadEmail = findLastUnreadEmail();
        String lastSubject = getEmailSubject(lastUnreadEmail);
        System.out.println("Subject: " + lastSubject);
        deleteEmail(lastUnreadEmail);
        verifyEmailDeletion(lastSubject);
//        verifyEmailDeletedInTrash(lastSubject);
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

        try {
            WebElement otpPrompt = wait.withTimeout(Duration.ofSeconds(3)).until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("div[data-challenge='TOTP']")));
            // found otp -> manual input, no other choice
            System.out.println("Manual OTP input required - waiting 30 seconds");
            Thread.sleep(30000);
        } catch (Exception e) {
            // Didn't find any otp prompt -> continue
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

    private void deleteEmail(WebElement emailElement){
        emailElement.findElement(By.cssSelector("div.oZ-jc")).click();
        wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("div[aria-label^='Delete']"))).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("span.bAq")));
//        wait.until(ExpectedConditions.stalenessOf(emailElement));
    }

    private void verifyEmailDeletion(String deletedEmailSubject) {
        List<WebElement> allEmails = driver.findElements(
                By.cssSelector("tr.zA.zE:not(.bqO)"));
        Boolean emailExists = allEmails.stream().anyMatch(email->
                email.findElement(By.cssSelector("span.bog"))
                        .getText().trim().equals(deletedEmailSubject));
        Assert.assertFalse(emailExists, "Supposedly deleted email still exist");
    }

//    private void verifyEmailDeletedInTrash(String deletedEmailSubject) {
//        driver.findElement(By.cssSelector("span.CJ")).click();
//        driver.findElement(By.cssSelector("div.TN.bzz.aHS-bnx")).click();
//        List<WebElement> allEmails = driver.findElements(
//                By.cssSelector("tr.zA.zE:not(.bqO)"));
//        for (WebElement mail : allEmails){
//            System.out.println(mail.findElement(By.cssSelector("span.bog")).getText().trim());
//        }
//        Boolean emailExists = allEmails.stream().anyMatch(email->
//                email.findElement(By.cssSelector("span.bog"))
//                        .getText().trim().equals(deletedEmailSubject));
//
//        Assert.assertFalse(emailExists, "Supposedly deleted email still exist");
//    }

}
