import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CardOrderFormTest {
    private WebDriver driver;

    @BeforeAll
    static void setupClass() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void setupTest() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--no-sandbox");
        options.addArguments("--headless");
        driver = new ChromeDriver(options);
        driver.get("http://localhost:9999");
    }

    @AfterEach
    void teardown() {
        driver.quit();
        driver = null;
    }

    // полная проверка всех полей
    @Test
    public void shouldSendFormCorrectly() {
        driver.findElement(By.cssSelector("[data-test-id=name] input")).sendKeys("Костромина Алина");
        driver.findElement(By.cssSelector("[data-test-id=phone] input")).sendKeys("+79139116136");
        driver.findElement(By.cssSelector("[data-test-id=agreement]")).click();
        driver.findElement(By.cssSelector("button")).click();
        String text = driver.findElement(By.cssSelector("[data-test-id=order-success]")).getText();

        assertEquals("Ваша заявка успешно отправлена! Наш менеджер свяжется с вами в ближайшее время.", text.trim());
    }

    // полная проверка всех полей (отправка пустых значений)
    @Test
    public void shouldSendFormIfFieldsEmpty() {
        driver.findElement(By.cssSelector("[data-test-id=name] input")).sendKeys("");
        driver.findElement(By.cssSelector("[data-test-id=phone] input")).sendKeys("");
        driver.findElement(By.cssSelector("[data-test-id=agreement]")).click();
        driver.findElement(By.cssSelector("button")).click();
        String actual = driver.findElement(By.cssSelector("[data-test-id=name].input_invalid .input__sub")).getText().trim();

        assertEquals("Поле обязательно для заполнения", actual);
    }

    // если имя на иностранном языке
    @Test
    public void shouldCheckNameIfEnglish() {
        driver.findElement(By.cssSelector("[data-test-id=name] input")).sendKeys("Alina");
        driver.findElement(By.cssSelector("[data-test-id=phone] input")).sendKeys("+79139116136");
        driver.findElement(By.cssSelector("[data-test-id=agreement]")).click();
        driver.findElement(By.cssSelector("button")).click();

        String actual = driver.findElement(By.cssSelector("[data-test-id=name].input_invalid .input__sub")).getText().trim();
        String expected = "Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы.";

        assertEquals(expected, actual);
    }

    // если в поле "Имя" ввести цифры
    @Test
    public void shouldCheckNameIfNumbers() {
        driver.findElement(By.cssSelector("[data-test-id=name] input")).sendKeys("8913911");
        driver.findElement(By.cssSelector("[data-test-id=phone] input")).sendKeys("+79139116136");
        driver.findElement(By.cssSelector("[data-test-id=agreement]")).click();
        driver.findElement(By.cssSelector("button")).click();

        String actual = driver.findElement(By.cssSelector("[data-test-id=name].input_invalid .input__sub")).getText().trim();
        String expected = "Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы.";

        assertEquals(expected, actual);
    }

    // если имя не заполнено
    @Test
    public void shouldNotSendIfEmptyName() {
        driver.findElement(By.cssSelector("[data-test-id=phone] input")).sendKeys("+79139116136");
        driver.findElement(By.cssSelector("[data-test-id=agreement]")).click();
        driver.findElement(By.cssSelector("button")).click();

        String actual = driver.findElement(By.cssSelector("[data-test-id=name].input_invalid .input__sub")).getText().trim();
        String expected = "Поле обязательно для заполнения";
        assertEquals(expected, actual);
    }

    // проверка номера телефона (меньше цифр, чем нужно)
    @Test
    public void shouldCheckPhone() {
        driver.findElement(By.cssSelector("[data-test-id=name] input")).sendKeys("Алина");
        driver.findElement(By.cssSelector("[data-test-id=phone] input")).sendKeys("+7913");
        driver.findElement(By.cssSelector("[data-test-id=agreement]")).click();
        driver.findElement(By.cssSelector("button")).click();

        String actual = driver.findElement(By.cssSelector("[data-test-id=phone].input_invalid .input__sub")).getText().trim();
        String expected = "Телефон указан неверно. Должно быть 11 цифр, например, +79012345678.";
        assertEquals(expected, actual);
    }

    // в поле "Телефон" ввести буквы
    @Test
    public void shouldCheckPhoneIfLetters() {
        driver.findElement(By.cssSelector("[data-test-id=name] input")).sendKeys("Алина");
        driver.findElement(By.cssSelector("[data-test-id=phone] input")).sendKeys("Алина");
        driver.findElement(By.cssSelector("[data-test-id=agreement]")).click();
        driver.findElement(By.cssSelector("button")).click();

        String actual = driver.findElement(By.cssSelector("[data-test-id=phone].input_invalid .input__sub")).getText().trim();
        String expected = "Телефон указан неверно. Должно быть 11 цифр, например, +79012345678.";
        assertEquals(expected, actual);
    }

    // если поле "Телефон" пустое
    @Test
    public void shouldNotSendIfEmptyPhone() {
        driver.findElement(By.cssSelector("[data-test-id=name] input")).sendKeys("Алина");
        driver.findElement(By.cssSelector("[data-test-id=agreement]")).click();
        driver.findElement(By.cssSelector("button")).click();

        String actual = driver.findElement(By.cssSelector("[data-test-id=phone].input_invalid .input__sub")).getText().trim();
        String expected = "Поле обязательно для заполнения";
        assertEquals(expected, actual);
    }

    // отжат чекбокс соглашения
    @Test
    public void shouldCheckAgreementIfDisabled() {
        driver.findElement(By.cssSelector("[data-test-id=name] input")).sendKeys("Алина");
        driver.findElement(By.cssSelector("[data-test-id=phone] input")).sendKeys("+12345678910");
        driver.findElement(By.cssSelector("button")).click();

        assertTrue(driver.findElement(By.cssSelector("[data-test-id=agreement].input_invalid")).isDisplayed());
    }
}
