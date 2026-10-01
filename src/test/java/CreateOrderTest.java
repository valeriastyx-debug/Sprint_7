import client.OrderClient;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import models.Order;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.apache.http.HttpStatus.SC_CREATED;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(Parameterized.class)
public class CreateOrderTest {

    private OrderClient orderClient;
    private final List<String> color;

    public CreateOrderTest(List<String> color) {
        this.color = color;
    }

    @Parameterized.Parameters(name = "Цвет самоката: {0}")
    public static Collection<Object[]> getTestData() {
        return Arrays.asList(new Object[][]{
                {Arrays.asList("BLACK")},
                {Arrays.asList("GREY")},
                {Arrays.asList("BLACK", "GREY")},
                {null}
        });
    }

    @Before
    public void setUp() {
        orderClient = new OrderClient();
    }

    @Test
    @DisplayName("Создание заказа с разными вариантами цвета")
    @Description("Проверяем создание заказа с BLACK, GREY, двумя цветами и без указания цвета")
    public void shouldCreateOrderWithDifferentColors() {

        Order order = new Order(
                "Ivan",
                "Ivanov",
                "Moscow, Lenina 10",
                4,
                "+79999999999",
                5,
                "2026-10-10",
                "Test order",
                color
        );

        Response response = orderClient.create(order);

        response.then()
                .statusCode(SC_CREATED)
                .body("track", notNullValue())
                .body("track", greaterThan(0));
    }
}