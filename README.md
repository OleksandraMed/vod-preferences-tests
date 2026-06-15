# VOD Preferences Tests

AI-assisted API and UI test automation for VOD onboarding flow.

---

## Description

This project contains automated tests for the **Profile Onboarding — VOD Preferences** feature.
The onboarding flow allows new users to select their favourite genres and movies,
which are then used to personalise their home screen recommendations.

80% of test artifacts (test cases and code) were generated using AI (Claude by Anthropic),
with a human acting as Reviewer and Prompt Engineer.

> **Note:** AI initially generated only API tests starting from TC-08 and missed TC-01, TC-02,
> TC-05, TC-06, TC-07 and the entire UI test layer. The Reviewer identified all gaps and
> corrected via follow-up prompts. See `PROMPTS.md` for full details.

---

## Tech Stack

| Tool | Purpose |
|------|---------|
| Java 17 | Programming language |
| Maven | Build tool |
| REST Assured 5.4.0 | API test framework |
| Playwright for Java 1.44.0 | UI test framework |
| TestNG 7.5 | Test runner |
| Allure 2.25.0 | Test reporting |
| Jackson | JSON serialization |

---

## Project Structure

```
vod-preferences-tests/
├── src/
│   └── test/
│       ├── java/
│       │   └── com/vod/
│       │       ├── config/
│       │       │   └── ApiConfig.java              # Base URL, auth token, profile IDs
│       │       ├── models/
│       │       │   └── VodPreferencesRequest.java  # Request body model
│       │       └── tests/
│       │           ├── BaseUiTest.java             # Playwright setup/teardown base class
│       │           ├── VodPreferencesApiTest.java  # API tests (group: api)
│       │           └── VodOnboardingUiTest.java    # UI tests (group: ui)
│       └── resources/
│           └── testng.xml                          # Test suite with API and UI groups
├── TEST-CASES.md                                   # All test cases (API + UI)
├── PROMPTS.md                                      # AI prompt log
├── AI-STRATEGY.md                                  # AI test generation and bug report strategy
├── pom.xml
└── README.md
```

---

## Test Coverage

### API Tests (group: api)

| Test | TC | Description | Type |
|------|----|-------------|------|
| testNewProfileHasOnboardingNotCompleted | TC-01 | New profile has onboarding_completed = false | Positive |
| testProfileOnboardingCompletedAfterSurvey | TC-02 | onboarding_completed = true after survey done | Positive |
| testMoviesByGenreReturnsFilteredContent | TC-06 | Movies endpoint filters by genre IDs | Positive |
| testSubmitExactlyFiveMovies | TC-07 | Submitting exactly 5 movies returns success | Positive |
| testSaveValidPreferences | TC-08 | Valid genres and movies saved successfully | Positive |
| testSavePreferencesWithTwoGenres | TC-09 | API returns 400 for fewer than 3 genres | Negative |
| testSavePreferencesWithEmptyLists | TC-10 | API returns 400 for empty body | Negative |
| testSavePreferencesForInvalidProfile | TC-11 | API returns 404 for non-existent profile | Negative |
| testSavePreferencesWithoutAuth | TC-12 | API returns 401 without auth token | Security |
| testSkipOnboardingReturnsDefaultRecommendations | TC-13 | Skip onboarding → recommendations return 200 | Positive |
| testRecommendationsUpdateAfterPreferences | TC-16 | Recommendations change after preferences saved | Integration |
| testSavePreferencesWithInvalidGenreIds | TC-17 | API returns 400 for invalid genre IDs | Negative |
| testSavePreferencesWithMissingMovieIds | TC-18 | API handles missing movie_ids field | Boundary |

### UI Tests (group: ui)

| Test | TC | Description | Type |
|------|----|-------------|------|
| testNextButtonDisabledWithLessThanThreeGenres | TC-03/TC-UI-01 | Next button disabled when < 3 genres selected | Negative |
| testNextButtonEnabledWithThreeGenres | TC-04/TC-UI-02 | Next button enabled when exactly 3 genres selected | Positive |
| testNextButtonRemainsEnabledWithMoreThanThreeGenres | TC-05 | Next button remains enabled with > 3 genres | Positive |

---

## How to Run

**Run all tests:**
```bash
mvn test
```

**Run only API tests:**
```bash
mvn test -Dgroups=api
```

**Run only UI tests:**
```bash
mvn test -Dgroups=ui
```

**Generate Allure report:**
```bash
mvn allure:serve
```

---

## Notes

- `BASE_URL` in `ApiConfig.java` is set to `https://api.example.com` (mock).
- UI tests use `https://example.com/onboarding` as a placeholder URL.
- Tests will fail with connection errors until connected to a real or mock server.
- All test artifacts were generated using AI. See `PROMPTS.md` for full prompt log.
