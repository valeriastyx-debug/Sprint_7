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

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.equalTo;

public class CreateCourierTest {

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

        if (loginResponse.statusCode() == SC_OK) {
            int courierId = loginResponse.jsonPath().getInt("id");

            courierClient.delete(courierId)
                    .then()
                    .statusCode(SC_OK);
        }
    }

    @Test
    @DisplayName("Успешное создание курьера со всеми обязательными полями")
    @Description("Проверяем, что курьера можно создать, код ответа 201 и возвращается ok: true")
    public void shouldCreateCourierSuccessfully() {
        Courier courier = new Courier(login, password, firstName);

        Response response = courierClient.create(courier);

        response.then()
                .statusCode(SC_CREATED)
                .body("ok", equalTo(true));
    }

    @Test
    @DisplayName("Нельзя создать двух одинаковых курьеров")
    @Description("Проверяем, что при попытке создать двух одинаковых курьеров возвращается ошибка")
    public void shouldNotCreateDuplicateCourier() {
        Courier courier = new Courier(login, password, firstName);

        courierClient.create(courier)
                .then()
                .statusCode(SC_CREATED)
                .body("ok", equalTo(true));

        Response response = courierClient.create(courier);

        response.then()
                .statusCode(SC_CONFLICT)
                .body(
                        "message",
                        equalTo("Этот логин уже используется. Попробуйте другой.")
                );
    }

    @Test
    @DisplayName("Нельзя создать курьера без логина")
    @Description("Проверяем ошибку 400 при отсутствии обязательного поля login")
    public void shouldNotCreateCourierWithoutLogin() {
        Courier courier = new Courier(null, password, firstName);

        Response response = courierClient.create(courier);

        response.then()
                .statusCode(SC_BAD_REQUEST)
                .body(
                        "message",
                        equalTo("Недостаточно данных для создания учетной записи")
                );
    }

    @Test
    @DisplayName("Нельзя создать курьера без пароля")
    @Description("Проверяем ошибку 400 при отсутствии обязательного поля password")
    public void shouldNotCreateCourierWithoutPassword() {
        Courier courier = new Courier(login, null, firstName);

        Response response = courierClient.create(courier);

        response.then()
                .statusCode(SC_BAD_REQUEST)
                .body(
                        "message",
                        equalTo("Недостаточно данных для создания учетной записи")
                );
    }

    @Test
    @DisplayName("Создание курьера с существующим логином")
    @Description("Проверяем, что при использовании существующего логина возвращается ошибка 409")
    public void shouldNotCreateCourierWithExistingLogin() {
        Courier firstCourier = new Courier(login, password, firstName);

        courierClient.create(firstCourier)
                .then()
                .statusCode(SC_CREATED)
                .body("ok", equalTo(true));

        Courier secondCourier =
                new Courier(login, "different_pass", "Petr");

        Response response = courierClient.create(secondCourier);

        response.then()
                .statusCode(SC_CONFLICT)
                .body(
                        "message",
                        equalTo("Этот логин уже используется. Попробуйте другой.")
                );
    }
}
