package tests;

import io.qameta.allure.Owner;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Owner("zhuravel")
public class WebShopTests {

    @BeforeEach
    void beforeEach() {
        RestAssured.filters(new AllureRestAssured());
        RestAssured.baseURI = "http://demowebshop.tricentis.com/";
    }

    @Test
    @DisplayName("01. Добавление товара 'Black & White Diamond Heart' в Корзину")
    void shouldAddProductToCart() {
        step("01) Нажать ссылку 'Jewelry' в меню 'CATEGORIES'", () -> {
            given()
                    .when()
                    .get("jewelry")
                    .then()
                    .statusCode(200);
        });
        step("02) Нажать кнопку 'Add to cart' в секции товара 'Black & White Diamond Heart' " +
                "и проверить, что количество товаров в Корзине равно '1'", () -> {
            String result = given()
                    .when()
                    .post("addproducttocart/catalog/14/1/1")
                    .then()
                    .statusCode(200)
                    .extract()
                    .path("updatetopcartsectionhtml");

            assertEquals("(1)", result);
        });
    }

    @Test
    @DisplayName("02. Удаление товара 'Wool Hat' из Корзины")
    void shouldDeleteProductFromCart() {
        step("01) Нажать ссылку 'Apparel & Shoes' в меню 'CATEGORIES'", () -> {
            given()
                    .when()
                    .get("apparel-shoes")
                    .then()
                    .statusCode(200);
        });
        step("02) Нажать кнопку 'Next' в меню 'CATEGORIES'", () -> {
            given()
                    .when()
                    .get("apparel-shoes?pagenumber=2")
                    .then()
                    .statusCode(200);
        });

        step("03) Нажать кнопку 'Add to cart' в секции товара 'Wool Hat'", () -> {
            given()
                    .when()
                    .get("indiana-jones-shapeable-wool-hat")
                    .then()
                    .statusCode(200);

        });
        step("04) Нажать кнопку 'Add to cart' на странице товара 'Wool Hat'", () -> {
            given()
                    .when()
                    .post("addproducttocart/details/34/1")
                    .then()
                    .statusCode(200);
        });
        step("05) Нажать на ссылку 'Shopping cart'", () -> {
            given()
                    .when()
                    .post("cart")
                    .then()
                    .statusCode(200);
        });
        step("06) Удалить товар 'Wool Hat' из Корзины и проверить, что количество товаров в Корзине равно '1'", () -> {
            String deleteRequestData = "src/test/resources/delete_request_data.json";
            Response responseBody = given()
                    .body(deleteRequestData)
                    .when()
                    .delete("cart")
                    .then()
                    .statusCode(200)
                    .extract().response();
            String count = responseBody.htmlPath().getString("**.find{it.@class == 'cart-qty'}");

            assertEquals("(0)", count);
        });
    }
}
