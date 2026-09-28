import client.CourierClient;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import models.Courier;
import models.CourierCredentials;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.notNullValue;

public class LoginCourierTest {

    private CourierClient courierClient;
    private String login;
    private String password;
    private String firstName;

    @Before
    public void setUp() {
        courierClient = new CourierClient();

        login = "courier_" + RandomStringUtils.randomAlphanumeric(8);
        password = "password123";
        firstName = "Ivan";
    }

    @After
    public void tearDown() {
        CourierCredentials credentials =
                new CourierCredentials(login, password);

        Response loginResponse = courierClient.login(credentials);

        if (loginResponse.statusCode() == 200) {
            int courierId = loginResponse.jsonPath().getInt("id");

            courierClient.delete(courierId)
                    .then()
                    .statusCode(200);
        }
    }

    @Test
    @DisplayName("Курьер может авторизоваться")
    @Description("Проверяем успешную авторизацию существующего курьера")
    public void shouldLoginCourierSuccessfully() {
        Courier courier = new Courier(login, password, firstName);

        courierClient.create(courier)
                .then()
                .statusCode(201);

        CourierCredentials credentials =
                new CourierCredentials(login, password);

        Response response = courierClient.login(credentials);

        response.then()
                .statusCode(200);
    }

    @Test
    @DisplayName("Успешная авторизация возвращает id")
    @Description("Проверяем, что после успешной авторизации API возвращает id курьера")
    public void shouldReturnIdAfterSuccessfulLogin() {
        Courier courier = new Courier(login, password, firstName);

        courierClient.create(courier)
                .then()
                .statusCode(201);

        CourierCredentials credentials =
                new CourierCredentials(login, password);

        Response response = courierClient.login(credentials);

        response.then()
                .statusCode(200)
                .body("id", notNullValue())
                .body("id", greaterThan(0));
    }

    @Test
    @DisplayName("Нельзя авторизоваться без логина")
    @Description("Проверяем ошибку при отсутствии обязательного поля login")
    public void shouldNotLoginWithoutLogin() {
        Map<String, String> credentials = new HashMap<>();
        credentials.put("password", password);

        Response response = courierClient.login(credentials);

        response.then()
                .statusCode(400);
    }

    @Test
    @DisplayName("Нельзя авторизоваться без пароля")
    @Description("Проверяем ошибку при отсутствии обязательного поля password")
    public void shouldNotLoginWithoutPassword() {
        Map<String, String> credentials = new HashMap<>();
        credentials.put("login", login);

        Response response = courierClient.login(credentials);

        response.then()
                .statusCode(400);
    }

    @Test
    @DisplayName("Нельзя авторизоваться с неправильным логином")
    @Description("Проверяем ошибку при передаче неправильного логина")
    public void shouldNotLoginWithWrongLogin() {
        Courier courier = new Courier(login, password, firstName);

        courierClient.create(courier)
                .then()
                .statusCode(201);

        CourierCredentials credentials =
                new CourierCredentials(login + "_wrong", password);

        Response response = courierClient.login(credentials);

        response.then()
                .statusCode(404);
    }

    @Test
    @DisplayName("Нельзя авторизоваться с неправильным паролем")
    @Description("Проверяем ошибку при передаче неправильного пароля")
    public void shouldNotLoginWithWrongPassword() {
        Courier courier = new Courier(login, password, firstName);

        courierClient.create(courier)
                .then()
                .statusCode(201);

        CourierCredentials credentials =
                new CourierCredentials(login, password + "_wrong");

        Response response = courierClient.login(credentials);

        response.then()
                .statusCode(404);
    }

    @Test
    @DisplayName("Нельзя авторизоваться несуществующим пользователем")
    @Description("Проверяем ошибку авторизации для курьера, которого нет в системе")
    public void shouldNotLoginNonExistingCourier() {
        CourierCredentials credentials =
                new CourierCredentials(
                        "nonexistent_" + RandomStringUtils.randomAlphanumeric(10),
                        "password123"
                );

        Response response = courierClient.login(credentials);

        response.then()
                .statusCode(404);
    }
}