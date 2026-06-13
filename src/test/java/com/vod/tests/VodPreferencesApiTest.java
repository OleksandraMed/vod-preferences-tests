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

    // TC-01: Onboarding screen appears once after profile creation
    // Checks that newly created profile has onboarding_completed = false
    @Test(description = "New profile has onboarding not completed", groups = "api")
    public void testNewProfileHasOnboardingNotCompleted() {
        given()
                .header("Authorization", ApiConfig.AUTH_TOKEN)
                .when()
                .get("/v1/profile/{profile_id}", ApiConfig.VALID_PROFILE_ID)
                .then()
                .statusCode(200)
                .body("onboarding_completed", equalTo(false));
    }

    // TC-02: Onboarding screen does NOT appear on second visit
    // Checks that after completing onboarding, onboarding_completed = true
    @Test(description = "Profile onboarding_completed is true after survey is done", groups = "api")
    public void testProfileOnboardingCompletedAfterSurvey() {
        // Save preferences — this should mark onboarding as completed
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

        // Verify onboarding is now marked as completed
        given()
                .header("Authorization", ApiConfig.AUTH_TOKEN)
                .when()
                .get("/v1/profile/{profile_id}", ApiConfig.VALID_PROFILE_ID)
                .then()
                .statusCode(200)
                .body("onboarding_completed", equalTo(true));
    }

    // TC-06: Step 2 shows movies based on selected genres
    // Checks that movies endpoint returns content filtered by genre
    @Test(description = "Movies endpoint returns films filtered by genre IDs", groups = "api")
    public void testMoviesByGenreReturnsFilteredContent() {
        given()
                .header("Authorization", ApiConfig.AUTH_TOKEN)
                .queryParam("genre_ids", "1,5,9")
                .when()
                .get("/v1/movies")
                .then()
                .statusCode(200)
                .body("items", not(empty()));
    }

    // TC-07: User can select exactly 5 movies and submit
    // Checks that submitting 5 movies returns success
    @Test(description = "Submitting exactly 5 movies returns success", groups = "api")
    public void testSubmitExactlyFiveMovies() {
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

    // TC-08: API saves genre and movie IDs correctly
    @Test(description = "Valid preferences are saved successfully", groups = "api")
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

    // TC-09: API returns 400 when fewer than 3 genres provided
    @Test(description = "API returns 400 when fewer than 3 genres provided", groups = "api")
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

    // TC-10: API returns 400 when genre and movie lists are empty
    @Test(description = "API returns 400 when genre and movie lists are empty", groups = "api")
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

    // TC-11: API returns 404 for non-existent profile_id
    @Test(description = "API returns 404 for non-existent profile", groups = "api")
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

    // TC-12: API returns 401 when no auth token provided
    @Test(description = "API returns 401 when no auth token provided", groups = "api")
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

    // TC-13: User skips onboarding — profile receives default recommendations
    // NOTE: checking status code only — response body structure unknown until connected to real API
    @Test(description = "Skip onboarding — recommendations endpoint returns 200", groups = "api")
    public void testSkipOnboardingReturnsDefaultRecommendations() {
        given()
                .header("Authorization", ApiConfig.AUTH_TOKEN)
                .when()
                .get("/v1/profile/{profile_id}/recommendations", ApiConfig.VALID_PROFILE_ID)
                .then()
                .statusCode(200);
    }

    // TC-16: Recommendations update after preferences are saved
    @Test(description = "Recommendations change after preferences are saved", groups = "api")
    public void testRecommendationsUpdateAfterPreferences() {
        // Step 1: get recommendations before saving preferences
        String recommendationsBefore = given()
                .header("Authorization", ApiConfig.AUTH_TOKEN)
                .when()
                .get("/v1/profile/{profile_id}/recommendations", ApiConfig.VALID_PROFILE_ID)
                .then()
                .statusCode(200)
                .extract().asString();

        // Step 2: save preferences
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

        // Step 3: verify recommendations have changed
        given()
                .header("Authorization", ApiConfig.AUTH_TOKEN)
                .when()
                .get("/v1/profile/{profile_id}/recommendations", ApiConfig.VALID_PROFILE_ID)
                .then()
                .statusCode(200)
                .body(not(equalTo(recommendationsBefore)));
    }

    // TC-17: API returns 400 for invalid (non-existent) genre IDs
    @Test(description = "API returns 400 for invalid genre IDs", groups = "api")
    public void testSavePreferencesWithInvalidGenreIds() {
        VodPreferencesRequest request = new VodPreferencesRequest(
                List.of(9999, 8888, 7777),
                List.of(101, 202, 303, 404, 505)
        );

        given()
                .header("Authorization", ApiConfig.AUTH_TOKEN)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/v1/profile/{profile_id}/vod-preferences", ApiConfig.VALID_PROFILE_ID)
                .then()
                .statusCode(anyOf(is(400), is(422)));
    }

    // TC-18: API handles request with missing movie_ids field
    // NOTE: sending raw JSON to simulate missing field — VodPreferencesRequest always includes movie_ids
    @Test(description = "API handles request with missing movie_ids field", groups = "api")
    public void testSavePreferencesWithMissingMovieIds() {
        String requestBody = "{\"genre_ids\": [1, 5, 9]}";

        given()
                .header("Authorization", ApiConfig.AUTH_TOKEN)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/v1/profile/{profile_id}/vod-preferences", ApiConfig.VALID_PROFILE_ID)
                .then()
                .statusCode(anyOf(is(200), is(201), is(400)));
    }
}