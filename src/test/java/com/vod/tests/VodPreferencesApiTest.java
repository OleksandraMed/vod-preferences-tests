package com.vod.tests;

import com.vod.config.ApiConfig;

import com.vod.models.VodPreferencesRequest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class VodPreferencesApiTest {
    @BeforeClass
    public void setup() {
        RestAssured.baseURI = ApiConfig.BASE_URL;
    }

    // TC-08: API зберігає жанри та фільми коректно
    @Test(description = "Valid preferences are saved successfully")
    public void testSaveValidPreferences() {
        VodPreferencesRequest request = new VodPreferencesRequest(
                List.of(1, 5, 9),
                List.of(101, 202, 303, 404, 505)
        );

        given()
                .header("Authorization", ApiConfig.AUTH_TOKEN)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/v1/profile/{profile_id}/vod-preferences", ApiConfig.VALID_PROFILE_ID)
                .then()
                .statusCode(anyOf(is(200), is(201)));
    }

    // TC-09: API повертає 400 якщо менше 3 жанрів
    @Test(description = "API returns 400 when fewer than 3 genres provided")
    public void testSavePreferencesWithTwoGenres() {
        VodPreferencesRequest request = new VodPreferencesRequest(
                List.of(1, 5),
                List.of(101, 202, 303, 404, 505)
        );

        given()
                .header("Authorization", ApiConfig.AUTH_TOKEN)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/v1/profile/{profile_id}/vod-preferences", ApiConfig.VALID_PROFILE_ID)
                .then()
                .statusCode(400);
    }

    // TC-10: API повертає 400 для порожнього тіла
    @Test(description = "API returns 400 when genre and movie lists are empty")
    public void testSavePreferencesWithEmptyLists() {
        VodPreferencesRequest request = new VodPreferencesRequest(
                List.of(),
                List.of()
        );

        given()
                .header("Authorization", ApiConfig.AUTH_TOKEN)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/v1/profile/{profile_id}/vod-preferences", ApiConfig.VALID_PROFILE_ID)
                .then()
                .statusCode(400);
    }

    // TC-11: API повертає 404 для неіснуючого profile_id
    @Test(description = "API returns 404 for non-existent profile")
    public void testSavePreferencesForInvalidProfile() {
        VodPreferencesRequest request = new VodPreferencesRequest(
                List.of(1, 5, 9),
                List.of(101, 202, 303, 404, 505)
        );

        given()
                .header("Authorization", ApiConfig.AUTH_TOKEN)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/v1/profile/{profile_id}/vod-preferences", ApiConfig.INVALID_PROFILE_ID)
                .then()
                .statusCode(404);
    }

    // TC-12: API повертає 401 без токена
    @Test(description = "API returns 401 when no auth token provided")
    public void testSavePreferencesWithoutAuth() {
        VodPreferencesRequest request = new VodPreferencesRequest(
                List.of(1, 5, 9),
                List.of(101, 202, 303, 404, 505)
        );

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/v1/profile/{profile_id}/vod-preferences", ApiConfig.VALID_PROFILE_ID)
                .then()
                .statusCode(401);
    }

    // TC-16: Рекомендації оновлюються після збереження вподобань
    @Test(description = "Recommendations change after preferences are saved")
    public void testRecommendationsUpdateAfterPreferences() {
        // Step 1: отримати рекомендації до збереження
        String recommendationsBefore = given()
                .header("Authorization", ApiConfig.AUTH_TOKEN)
                .when()
                .get("/v1/profile/{profile_id}/recommendations", ApiConfig.VALID_PROFILE_ID)
                .then()
                .statusCode(200)
                .extract().asString();

        // Step 2: зберегти вподобання
        VodPreferencesRequest request = new VodPreferencesRequest(
                List.of(1, 5, 9),
                List.of(101, 202, 303, 404, 505)
        );

        given()
                .header("Authorization", ApiConfig.AUTH_TOKEN)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/v1/profile/{profile_id}/vod-preferences", ApiConfig.VALID_PROFILE_ID)
                .then()
                .statusCode(anyOf(is(200), is(201)));

        // Step 3: перевірити що рекомендації змінились
        given()
                .header("Authorization", ApiConfig.AUTH_TOKEN)
                .when()
                .get("/v1/profile/{profile_id}/recommendations", ApiConfig.VALID_PROFILE_ID)
                .then()
                .statusCode(200)
                .body(not(equalTo(recommendationsBefore)));
    }
}
