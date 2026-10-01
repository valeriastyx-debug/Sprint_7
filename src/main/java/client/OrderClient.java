package client;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import models.Order;

import static io.restassured.RestAssured.given;

public class OrderClient {

    private static final String ORDER_PATH =
            "/api/v1/orders";

    @Step("Создание заказа")
    public Response create(Order order) {
        return given()
                .spec(CourierClient.getBaseSpec())
                .body(order)
                .when()
                .post(ORDER_PATH);
    }

    @Step("Получение списка заказов")
    public Response getOrders() {
        return given()
                .spec(CourierClient.getBaseSpec())
                .when()
                .get(ORDER_PATH);
    }
}