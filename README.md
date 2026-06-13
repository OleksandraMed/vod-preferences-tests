# VOD Preferences Tests

AI-assisted API and UI test automation for VOD onboarding flow.

---

## Description

This project contains automated tests for the **Profile Onboarding — VOD Preferences** feature.
The onboarding flow allows new users to select their favourite genres and movies,
which are then used to personalise their home screen recommendations.

80% of test artifacts (test cases and code) were generated using AI (Claude by Anthropic),
with a human acting as Reviewer and Prompt Engineer.

> **Note:** AI initially generated only API tests and missed the UI test layer requirement.
> The Reviewer identified this gap and prompted AI to add Playwright UI tests.
> See `PROMPTS.md` for full details.

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
├── AI-STRATEGY.md                                  # AI test generation strategy
├── pom.xml
└── README.md
```

---

## Test Coverage

### API Tests (group: api)

| Test | Description | Type |
|------|-------------|------|
| testSaveValidPreferences | Valid genres and movies saved successfully | Positive |
| testSavePreferencesWithTwoGenres | API returns 400 for fewer than 3 genres | Negative |
| testSavePreferencesWithEmptyLists | API returns 400 for empty body | Negative |
| testSavePreferencesForInvalidProfile | API returns 404 for non-existent profile | Negative |
| testSavePreferencesWithoutAuth | API returns 401 without auth token | Security |
| testRecommendationsUpdateAfterPreferences | Recommendations change after preferences saved | Integration |

### UI Tests (group: ui)

| Test | Description | Type |
|------|-------------|------|
| testNextButtonDisabledWithLessThanThreeGenres | Next button disabled when < 3 genres selected | Negative |
| testNextButtonEnabledWithThreeGenres | Next button enabled when exactly 3 genres selected | Positive |

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

---

## Notes

- `BASE_URL` in `ApiConfig.java` is set to `https://api.example.com` (mock).
- UI tests use `https://example.com/onboarding` as a placeholder URL.
- Tests will fail with connection errors until connected to a real or mock server.
- All test artifacts were generated using AI. See `PROMPTS.md` for full prompt log.
