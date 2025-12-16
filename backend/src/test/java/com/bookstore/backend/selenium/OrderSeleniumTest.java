package com.bookstore.backend.selenium;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import java.util.List;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OrderSeleniumTest {

  private WebDriver driver;
  private WebDriverWait wait;

  private static final String BASE_URL = "http://localhost:5173";
  private static final String BOOK_DETAIL_URL = BASE_URL + "/book/tam-ly-hoc-cho-lua-tuoi-30";

  private static final String CUSTOMER_EMAIL = "quanglam@gmail.com";
  private static final String PASSWORD = "456789";

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

  // ================== HELPER ==================

  private void loginAndClearCart() {
    driver.get(BASE_URL + "/login");

    wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("email")))
        .sendKeys(CUSTOMER_EMAIL);
    driver.findElement(By.name("password")).sendKeys(PASSWORD);

    WebElement loginButton =
        wait.until(
            ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(text(),'Đăng nhập')]")));
    loginButton.click();

    wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("/login")));

    // xóa hết cart
    driver.get(BASE_URL + "/cart");
    List<WebElement> removeBtns = driver.findElements(By.cssSelector("[data-testid='btn-remove']"));
    for (WebElement btn : removeBtns) {
      WebElement item = btn.findElement(By.xpath("./ancestor::div[contains(@class,'w-full')]"));
      ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
      wait.until(ExpectedConditions.stalenessOf(item));
    }
  }

  private void addBookToCart() {
    driver.get(BOOK_DETAIL_URL);
    WebElement addBtn =
        wait.until(
            ExpectedConditions.elementToBeClickable(
                By.cssSelector("[data-testid='btn-add-to-cart']")));
    addBtn.click();

    // chờ toast biến mất
    try {
      WebElement toast =
          new WebDriverWait(driver, Duration.ofSeconds(5))
              .until(
                  ExpectedConditions.presenceOfElementLocated(By.cssSelector(".Toastify__toast")));
      new WebDriverWait(driver, Duration.ofSeconds(5)).until(ExpectedConditions.stalenessOf(toast));
    } catch (TimeoutException ignored) {
    }
  }

  // ================== TEST CASE ==================

  @Test
  @Order(1)
  @DisplayName("TC_ORD_003 - Đặt hàng (tạo đơn)")
  void TC_ORD_003_createOrder() {
    loginAndClearCart();
    addBookToCart();

    // Vào giỏ hàng
    driver.get(BASE_URL + "/cart");

    // Click nút Thanh toán
    WebElement checkoutBtn =
        wait.until(
            ExpectedConditions.elementToBeClickable(
                By.cssSelector("[data-testid='btn-checkout']")));
    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", checkoutBtn);
    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", checkoutBtn);

    // Chờ trang checkout
    wait.until(ExpectedConditions.urlContains("/checkout"));

    // Lấy danh sách select
    List<WebElement> addressSelects = driver.findElements(By.cssSelector("select"));

    for (WebElement selectElem : addressSelects) {
      Select select = new Select(selectElem);
      List<WebElement> options = select.getOptions();
      if (!options.isEmpty()) {
        // Chọn option đầu tiên (không phải option placeholder)
        if (options.size() > 1) {
          select.selectByIndex(1); // chọn option thứ 2 (index 1) nếu index 0 là placeholder
        } else {
          select.selectByIndex(0); // nếu chỉ có 1 option thì chọn luôn
        }
      }
    }

    // Nếu không có địa chỉ lưu trữ nào được chọn
    if (!addressSelects.isEmpty() && addressSelects.get(0).getAttribute("value").isEmpty()) {

      // Nhập thông tin cơ bản
      driver.findElement(By.name("fullname")).sendKeys("Quang Lam");
      driver.findElement(By.name("phone")).sendKeys("0912345678");
      driver.findElement(By.name("speaddress")).sendKeys("123 Đường Lê Lợi");

      // Nhập city
      WebElement cityElem = driver.findElement(By.name("city"));

      // Chờ city enable
      wait.until(
          driver -> {
            String disabled = cityElem.getAttribute("disabled");
            return disabled == null || disabled.equals("false");
          });

      // Chọn city
      Select citySelect = new Select(cityElem);
      citySelect.selectByVisibleText("Hà Nội");

      // Ward
      WebElement wardElem = driver.findElement(By.name("ward"));

      // Chờ ward enable và có ít nhất 1 option
      wait.until(
          driver -> {
            String disabled = wardElem.getAttribute("disabled");
            List<WebElement> options = wardElem.findElements(By.tagName("option"));
            return (disabled == null || disabled.equals("false")) && options.size() > 1;
          });

      // Chọn ward
      Select wardSelect = new Select(wardElem);
      wardSelect.selectByVisibleText("Phường Hoàng Kiếm");
    }

    // Chọn COD
    WebElement codLabel = driver.findElement(By.cssSelector("label[for='paymethod-cod']"));
    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", codLabel);
    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", codLabel);

    // Click nút Đặt hàng
    WebElement submitBtn = driver.findElement(By.xpath("//button[contains(text(),'Đặt hàng')]"));
    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", submitBtn);
    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", submitBtn);

    // Chờ redirect trang result
    wait.until(ExpectedConditions.urlContains("/order-result?result=successful"));

    assertTrue(
        driver.getCurrentUrl().contains("/order-result?result=successful"),
        "Đơn hàng phải tạo thành công");
  }
}
