# VOD Preferences Tests

AI-assisted API test automation for VOD onboarding flow.

---

## Description

This project contains automated API tests for the **Profile Onboarding — VOD Preferences** feature.
The onboarding flow allows new users to select their favourite genres and movies,
which are then used to personalise their home screen recommendations.

80% of test artifacts (test cases and code) were generated using AI (Claude by Anthropic),
with a human acting as Reviewer and Prompt Engineer.

---

## Tech Stack

| Tool | Purpose |
|------|---------|
| Java 17 | Programming language |
| Maven | Build tool |
| REST Assured 5.4.0 | API test framework |
| TestNG 7.9.0 | Test runner |
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
│       │       │   └── ApiConfig.java          # Base URL, auth token, profile IDs
│       │       ├── models/
│       │       │   └── VodPreferencesRequest.java  # Request body model
│       │       └── tests/
│       │           └── VodPreferencesApiTest.java  # API test cases
│       └── resources/
│           └── testng.xml                      # Test suite configuration
├── pom.xml
├── PROMPTS.md                                  # AI prompt log
├── AI-STRATEGY.md                              # AI test generation strategy
└── README.md
```

---

## Test Coverage

| Test | Description | Type |
|------|-------------|------|
| testSaveValidPreferences | Valid genres and movies saved successfully | Positive |
| testSavePreferencesWithTwoGenres | API returns 400 for fewer than 3 genres | Negative |
| testSavePreferencesWithEmptyLists | API returns 400 for empty body | Negative |
| testSavePreferencesForInvalidProfile | API returns 404 for non-existent profile | Negative |
| testSavePreferencesWithoutAuth | API returns 401 without auth token | Security |
| testRecommendationsUpdateAfterPreferences | Recommendations change after preferences saved | Integration |

---

## How to Run

```bash
mvn test
```

---

## Notes

- `BASE_URL` in `ApiConfig.java` is set to `https://api.example.com` (mock).
- Tests will fail with `UnknownHostException` until connected to a real or mock server.
- All test artifacts were generated using AI. See `PROMPTS.md` for full prompt log.
