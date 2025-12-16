package com.bookstore.backend.selenium;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import java.util.UUID;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AccessControlSeleniumTest {

  private WebDriver driver;
  private WebDriverWait wait;

  private static final String BASE_URL = "http://localhost:5173";

  private static final String SYSADMIN_EMAIL = "admin456@gmail.com";
  private static final String STAFF_EMAIL = "admin456789@gmail.com";
  private static final String CUSTOMER_EMAIL = "quanglam@gmail.com";
  private static final String PASSWORD = "456789";

  private static final String TEMP_CUSTOMER_FULLNAME = "Khách Hàng Mới";
  private static final String TEMP_REGISTER_EMAIL =
      "temp_" + UUID.randomUUID().toString().substring(0, 6) + "@test.com";

  // ================= SETUP =================

  @BeforeEach
  void setUp() {
    driver = new ChromeDriver();
    driver.manage().window().maximize();
    wait = new WebDriverWait(driver, Duration.ofSeconds(15));
  }

  @AfterEach
  void tearDown() {
    if (driver != null) driver.quit();
  }

  // ================= HELPERS =================

  private void loginClient(String email, String password) {
    driver.get(BASE_URL + "/login");

    wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("email"))).sendKeys(email);
    driver.findElement(By.name("password")).sendKeys(password);
    WebElement loginButton =
        wait.until(
            ExpectedConditions.elementToBeClickable(
                By.xpath("//button[@type='submit' and contains(text(), 'Đăng nhập')]")));

    loginButton.click();

    wait.until(ExpectedConditions.not(ExpectedConditions.urlToBe(BASE_URL + "/login")));
  }

  private void loginAdmin(String email, String password) {
    driver.get(BASE_URL + "/admin/login");

    wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("email"))).sendKeys(email);
    driver.findElement(By.name("password")).sendKeys(password);
    WebElement loginButton =
        wait.until(
            ExpectedConditions.elementToBeClickable(
                By.xpath("//button[@type='submit' and contains(text(), 'Đăng nhập')]")));

    loginButton.click();

    wait.until(ExpectedConditions.urlMatches(".*/admin($|/.*)"));
  }

  private void openClientProfileMenu() {
    WebElement profileWrapper =
        wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("div.cursor-pointer.group")));

    Actions actions = new Actions(driver);

    // Hover vào icon user
    actions.moveToElement(profileWrapper).perform();

    // Chờ menu xuất hiện
    WebElement logoutButton =
        wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//button[normalize-space()='Đăng xuất']")));

    // Di chuyển chuột vào menu để tránh mouseleave
    actions.moveToElement(logoutButton).pause(Duration.ofMillis(200)).perform();
  }

  // ================= TEST CASES =================

  @Test
  @Order(1)
  @DisplayName("TC_AC_001 - Khách hàng đăng nhập thành công")
  void TC_AC_001_customerLogin_Success() {
    loginClient(CUSTOMER_EMAIL, PASSWORD);
    openClientProfileMenu();

    assertTrue(
        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//button[normalize-space()='Đăng xuất']")))
            .isDisplayed());
  }

  @Test
  @Order(2)
  @DisplayName("TC_AC_002 - Quản trị viên đăng nhập thành công")
  void TC_AC_002_adminLogin_Success() {
    loginAdmin(SYSADMIN_EMAIL, PASSWORD);
    assertTrue(driver.getCurrentUrl().contains("/admin"));

    driver.quit();
    setUp();

    loginAdmin(STAFF_EMAIL, PASSWORD);
    assertTrue(driver.getCurrentUrl().contains("/admin"));
  }

  @Test
  @Order(4)
  @DisplayName("TC_AC_005 - Đăng ký thành công")
  void TC_AC_005_register_Success() {
    driver.get(BASE_URL + "/register");

    wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("fullname")))
        .sendKeys(TEMP_CUSTOMER_FULLNAME);
    driver.findElement(By.name("email")).sendKeys(TEMP_REGISTER_EMAIL);
    driver.findElement(By.name("password")).sendKeys(PASSWORD);

    WebElement registerButton =
        wait.until(
            ExpectedConditions.elementToBeClickable(
                By.xpath("//button[@type='submit' and contains(text(), 'Đăng ký')]")));

    registerButton.click();

    wait.until(ExpectedConditions.urlToBe(BASE_URL + "/login"));
    assertEquals(BASE_URL + "/login", driver.getCurrentUrl());
  }

  @Test
  @Order(5)
  @DisplayName("TC_AC_007 - Đăng xuất thành công")
  void TC_AC_007_logout_Success() {
    loginClient(CUSTOMER_EMAIL, PASSWORD);
    openClientProfileMenu();

    WebElement logoutButton =
        wait.until(
            ExpectedConditions.elementToBeClickable(
                By.xpath("//button[normalize-space()='Đăng xuất']")));
    logoutButton.click();

    // Chờ logout xong
    wait.until(
        ExpectedConditions.or(
            ExpectedConditions.urlContains("/login"), ExpectedConditions.urlToBe(BASE_URL + "/")));

    // Hover lại icon user
    WebElement profileIcon =
        wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("div.cursor-pointer.group")));
    new Actions(driver).moveToElement(profileIcon).perform();

    // Kiểm tra nút Đăng nhập
    assertTrue(
        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//a[normalize-space()='Đăng nhập']")))
            .isDisplayed());
  }
}
