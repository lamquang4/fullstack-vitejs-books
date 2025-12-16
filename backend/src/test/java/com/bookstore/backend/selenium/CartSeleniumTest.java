package com.bookstore.backend.selenium;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import java.util.List;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CartSeleniumTest {

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

    // chờ icon cart hiển thị
    wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("[data-testid='cart']")));

    // mở cart
    driver.get(BASE_URL + "/cart");
    wait.until(ExpectedConditions.urlContains("/cart"));

    // xóa hết item nếu có
    List<WebElement> removeBtns = driver.findElements(By.cssSelector("[data-testid='btn-remove']"));
    for (WebElement btn : removeBtns) {
      WebElement item = btn.findElement(By.xpath("./ancestor::div[contains(@class,'w-full')]"));
      btn.click();
      wait.until(ExpectedConditions.stalenessOf(item));
    }

    driver.get(BASE_URL + "/");
  }

  private void waitForToastIfExists() {
    try {
      WebElement toast =
          new WebDriverWait(driver, Duration.ofSeconds(5))
              .until(
                  ExpectedConditions.presenceOfElementLocated(By.cssSelector(".Toastify__toast")));
      new WebDriverWait(driver, Duration.ofSeconds(5)).until(ExpectedConditions.stalenessOf(toast));
    } catch (TimeoutException ignored) {
    }
  }

  private void addBookToCart() {
    driver.get(BOOK_DETAIL_URL);

    wait.until(
            ExpectedConditions.elementToBeClickable(
                By.cssSelector("[data-testid='btn-add-to-cart']")))
        .click();

    waitForToastIfExists();
  }

  private WebElement getCartItem() {
    driver.get(BASE_URL + "/cart");
    // chờ item xuất hiện
    return wait.until(
        driver -> {
          List<WebElement> items = driver.findElements(By.cssSelector("div.w-full"));
          return items.isEmpty() ? null : items.get(0);
        });
  }

  // ================== TEST CASES ==================

  @Test
  @Order(1)
  @DisplayName("TC_CART_001 - Thêm sản phẩm vào giỏ")
  void TC_CART_001_addItemToCart() {
    loginAndClearCart();
    addBookToCart();

    WebElement cartItem = getCartItem();
    assertTrue(cartItem.isDisplayed());
  }
}