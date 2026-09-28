package client;

import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import models.Courier;
import models.CourierCredentials;

import static io.restassured.RestAssured.given;

public class CourierClient {

    private static final String BASE_URL =
            "https://qa-scooter.praktikum-services.ru";

    private static final String ROOT_PATH =
            "/api/v1/courier";

    private static final String LOGIN_PATH =
            "/api/v1/courier/login";

    public static RequestSpecification getBaseSpec() {
        return new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setBaseUri(BASE_URL)
                .addFilter(new AllureRestAssured())
                .build();
    }

    @Step("Создание курьера")
    public Response create(Courier courier) {
        return given()
                .spec(getBaseSpec())
                .body(courier)
                .when()
                .post(ROOT_PATH);
    }

    @Step("Авторизация курьера")
    public Response login(CourierCredentials credentials) {
        return given()
                .spec(getBaseSpec())
                .body(credentials)
                .when()
                .post(LOGIN_PATH);
    }

    @Step("Авторизация курьера с неполными данными")
    public Response login(Object credentials) {
        return given()
                .spec(getBaseSpec())
                .body(credentials)
                .when()
                .post(LOGIN_PATH);
    }

    @Step("Удаление курьера с id = {courierId}")
    public Response delete(int courierId) {
        return given()
                .spec(getBaseSpec())
                .when()
                .delete(ROOT_PATH + "/" + courierId);
    }
}