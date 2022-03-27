package com.asgarov.domain;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testcontainers.containers.BrowserWebDriverContainer;
import org.testcontainers.containers.VncRecordingContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.testcontainers.containers.BrowserWebDriverContainer.VncRecordingMode.RECORD_ALL;

@Testcontainers
public class WebsiteTestWithSelenium {

    private static final File tmpDirectory = new File("target");

    @Container
    public static BrowserWebDriverContainer<?> chrome =
            new BrowserWebDriverContainer<>()
                    .withCapabilities(getOptions())
                    .withRecordingMode(RECORD_ALL,
                            tmpDirectory,
                            VncRecordingContainer.VncRecordingFormat.MP4);


    private static ChromeOptions getOptions() {
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--no-sandbox", "--disable-dev-shm-usage");
        return chromeOptions;
    }

    @Test
    public void checkTheSiteOut() {
        RemoteWebDriver driver = chrome.getWebDriver();
        driver.get("https://github.com/asgarov1");

        WebElement title = driver.findElement(By.tagName("h1"));
        assertEquals("Javid Asgarov\nasgarov1", title.getText());

        File screen = driver.getScreenshotAs(OutputType.FILE);
    }


}