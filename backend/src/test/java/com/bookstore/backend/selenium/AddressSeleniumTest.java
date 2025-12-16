package com.bookstore.backend.selenium;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AddressSeleniumTest {

  private WebDriver driver;
  private WebDriverWait wait;

  private static final String BASE_URL = "http://localhost:5173";

  private static final String EMAIL = "quanglam@gmail.com";
  private static final String PASSWORD = "456789";

  private static final String FULLNAME = "Nguyễn Văn A";
  private static final String PHONE = "0987654321";
  private static final String SPECIFIC_ADDRESS = "123 Nguyễn Trãi";

  private static final String CITY_KEYWORD = "Hà Nội";
  private static final String WARD_KEYWORD = "Phường Hoàn Kiếm";

  private static final String UPDATED_ADDRESS = "100/1 Hẻm ABC";

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

  private void login() {
    driver.get(BASE_URL + "/login");

    wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("email"))).sendKeys(EMAIL);
    driver.findElement(By.name("password")).sendKeys(PASSWORD);

    WebElement loginButton =
        wait.until(
            ExpectedConditions.elementToBeClickable(
                By.xpath("//button[@type='submit' and contains(text(), 'Đăng nhập')]")));

    loginButton.click();

    wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("/login")));
  }

  private void openAddressPage() {
    driver.get(BASE_URL + "/address");

    wait.until(
        ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//button[contains(text(),'Thêm địa chỉ')]")));
  }

  /** HÀM CHỌN CITY + WARD CHUẨN REACT (KHÔNG DÙNG Select) */
  private void selectByKeyword(String selectName, String keyword) {
    WebElement select =
        wait.until(ExpectedConditions.presenceOfElementLocated(By.name(selectName)));

    wait.until(driver -> select.isEnabled());

    new WebDriverWait(driver, Duration.ofSeconds(10))
        .until(
            d -> {
              try {
                return select.findElements(By.tagName("option")).stream()
                    .anyMatch(opt -> opt.getText().contains(keyword));
              } catch (StaleElementReferenceException e) {
                return false;
              }
            });

    JavascriptExecutor js = (JavascriptExecutor) driver;
    js.executeScript(
        """
                                    const select = arguments[0];
                                    const keyword = arguments[1];
                                    for (const opt of select.options) {
                                        if (opt.text.includes(keyword)) {
                                            select.value = opt.value;
                                            select.dispatchEvent(new Event('change', { bubbles: true }));
                                            return;
                                        }
                                    }
                                    throw 'Option not found: ' + keyword;
                                """,
        select,
        keyword);
  }

  private void addAddressSuccess() {
    wait.until(
            ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(text(),'Thêm địa chỉ')]")))
        .click();

    wait.until(
        ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//h4[contains(text(),'Địa chỉ của bạn')]")));

    driver.findElement(By.name("fullname")).sendKeys(FULLNAME);
    driver.findElement(By.name("phone")).sendKeys(PHONE);
    driver.findElement(By.name("speaddress")).sendKeys(SPECIFIC_ADDRESS);

    selectByKeyword("city", CITY_KEYWORD);
    wait.until(
        driver ->
            driver.findElement(By.name("ward")).findElements(By.tagName("option")).size() > 1);

    selectByKeyword("ward", WARD_KEYWORD);

    driver.findElement(By.xpath("//button[contains(text(),'Lưu')]")).click();
  }

  // ================= TEST CASES =================

  @Test
  @Order(1)
  @DisplayName("TC_ADD_001 - Thêm địa chỉ thành công")
  void TC_ADD_001_addAddressSuccess() {
    login();
    openAddressPage();

    addAddressSuccess();

    openAddressPage();

    WebElement addedItem =
        wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[contains(text(),'" + SPECIFIC_ADDRESS + "')]")));
    assertTrue(addedItem.isDisplayed(), "Địa chỉ phải được thêm vào danh sách");
  }

  @Test
  @Order(2)
  @DisplayName("TC_ADD_005 - Chỉnh sửa địa chỉ thành công")
  void TC_ADD_005_editAddressSuccess() {
    login();
    openAddressPage();

    WebElement editButton =
        wait.until(
            ExpectedConditions.elementToBeClickable(
                By.xpath("//button[normalize-space()='Chỉnh sửa']")));
    editButton.click();

    WebElement specificAddressInput =
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("speaddress")));

    specificAddressInput.sendKeys(Keys.CONTROL + "a");
    specificAddressInput.sendKeys(Keys.DELETE);
    specificAddressInput.sendKeys(UPDATED_ADDRESS);

    driver.findElement(By.xpath("//button[normalize-space()='Lưu']")).click();

    WebElement updatedItem =
        wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[contains(text(),'" + UPDATED_ADDRESS + "')]")));

    assertTrue(updatedItem.isDisplayed(), "Địa chỉ phải được cập nhật trong danh sách");
  }

  @Test
  @Order(3)
  @DisplayName("TC_ADD_006 - Xóa địa chỉ thành công")
  void TC_ADD_006_deleteAddressSuccess() {
    login();
    openAddressPage();

    WebElement addressText =
        wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//p[contains(text(),'Địa chỉ:')]")));
    String addressContent = addressText.getText();

    WebElement deleteButton =
        addressText.findElement(
            By.xpath("./ancestor::div[contains(@class,'flex')]//button[normalize-space()='Xóa']"));
    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", deleteButton);
    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", deleteButton);

    WebElement confirmButton =
        wait.until(
            ExpectedConditions.elementToBeClickable(
                By.xpath("//button[normalize-space()='Xác nhận' or normalize-space()='Đồng ý']")));
    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", confirmButton);

    wait.until(
        ExpectedConditions.invisibilityOfElementLocated(
            By.xpath("//*[contains(text(),'" + addressContent + "')]")));

    assertTrue(true, "Địa chỉ đã bị xóa khỏi danh sách");
  }
}

