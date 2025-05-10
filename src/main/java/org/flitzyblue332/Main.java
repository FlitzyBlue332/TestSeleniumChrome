package org.flitzyblue332;


import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--incognito");
        WebDriver driver = new ChromeDriver(options);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        String EMAIL = "arturiagiallo332@gmail.com";
        String PASSWORD = "Char245Cello";

        driver.get("https://mail.google.com");
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
            WebElement otpPrompt = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("div[data-challenge='TOTP']")));
            System.out.println("Manual OTP input required - waiting 30 seconds");
            // found otp -> manual input, no other choice
            Thread.sleep(30000);
        } catch (Exception e) {
            // Didn't find any otp prompt -> continue
        }

        // get email
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("div[role='main']")));
//        List<WebElement> unreadEmails = driver.findElements(By.cssSelector("tr.zA.zE:not(.bqO)"));
        List<WebElement> unreadEmails = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                        By.cssSelector("tr.zA.zE:not(.bqO)")));
        WebElement top = unreadEmails.getFirst();
        String text = top.findElement(By.cssSelector("span.bog")).getText().trim();
        System.out.println(text);
//        top.findElement(By.cssSelector("div.oZ-jc")).click();
//        driver.findElement(By.cssSelector("div[aria-label^='Delete']")).click();
////        wait.until(ExpectedConditions.elementToBeClickable(
////                By.cssSelector("div[aria-label^='Delete']")));
//        wait.until(ExpectedConditions.invisibilityOf(
//                top.findElement(By.cssSelector("span.bog"))));
    }


}