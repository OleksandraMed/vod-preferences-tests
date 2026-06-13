# Test Cases: Profile Onboarding — VOD Preferences

> Generated via AI (Claude) based on User Story requirements.
> Format: structured for TMS import (TestRail-compatible).
> UI test cases (TC-UI-*) added manually after AI missed the UI layer requirement.

---

## 🔵 API TEST CASES

### TC-01 — Onboarding screen appears once after profile creation

**Type:** Positive | **Priority:** High | **Layer:** E2E | **Group:** api

**Preconditions:** User is authenticated; new profile just created.

**Steps:**
1. Create a new profile via API or UI.
2. Open the home page / trigger onboarding screen.

**Expected Result:** Onboarding survey screen is shown automatically.

---

### TC-02 — Onboarding screen does NOT appear on second visit

**Type:** Negative | **Priority:** High | **Layer:** E2E | **Group:** api

**Preconditions:** User has already seen and completed/skipped the onboarding screen.

**Steps:**
1. Complete or skip the survey on first visit.
2. Log out and log back in.
3. Open the home page again.

**Expected Result:** Onboarding screen is NOT shown again.

---

### TC-03 — "Next" button is disabled when fewer than 3 genres selected

**Type:** Negative | **Priority:** High | **Layer:** E2E / UI Logic | **Group:** api

**Preconditions:** Onboarding screen is visible; Step 1 (genre selection).

**Steps:**
1. Select 0, 1, or 2 genres.
2. Observe "Next" button state.

**Expected Result:** "Next" button remains inactive/disabled for 0, 1, and 2 selected genres.

---

### TC-04 — "Next" button activates when exactly 3 genres are selected

**Type:** Positive (Boundary) | **Priority:** High | **Layer:** E2E / UI Logic | **Group:** api

**Preconditions:** Onboarding screen is visible; Step 1.

**Steps:**
1. Select exactly 3 genres.
2. Observe "Next" button state.

**Expected Result:** "Next" button becomes active/enabled.

---

### TC-05 — "Next" button remains active when more than 3 genres are selected

**Type:** Positive | **Priority:** Medium | **Layer:** E2E / UI Logic | **Group:** api

**Preconditions:** Onboarding screen is visible; Step 1.

**Steps:**
1. Select 4 or 5 genres.
2. Observe "Next" button state.

**Expected Result:** "Next" button remains active/enabled.

---

### TC-06 — Step 2 shows movies based on selected genres

**Type:** Positive | **Priority:** High | **Layer:** E2E / API | **Group:** api

**Preconditions:** User selected 3+ genres on Step 1 and clicked "Next".

**Steps:**
1. Select 3+ genres on Step 1.
2. Click "Next".
3. Observe content on Step 2.

**Expected Result:** Step 2 displays a list of movies filtered by the genres selected in Step 1.

---

### TC-07 — User can select exactly 5 movies on Step 2

**Type:** Positive | **Priority:** High | **Layer:** E2E | **Group:** api

**Preconditions:** User is on Step 2.

**Steps:**
1. Select exactly 5 movies.
2. Click "Finish" / "Submit".

**Expected Result:** Preferences are saved; user is redirected to the home page with personalised recommendations.

---

### TC-08 — API saves selected genre and movie IDs correctly

**Type:** Positive | **Priority:** Critical | **Layer:** API | **Group:** api

**Preconditions:** Valid profile_id exists; valid genre IDs and movie IDs are known.

**Steps:**
1. Send `POST /v1/profile/{profile_id}/vod-preferences` with body:
```json
{
  "genre_ids": [1, 5, 9],
  "movie_ids": [101, 202, 303, 404, 505]
}
```
2. Check response status and body.
3. Query DB or recommendations endpoint for this profile.

**Expected Result:**
- Response: `200 OK` or `201 Created`.
- DB contains the submitted genre and movie IDs for this profile.
- Recommendations endpoint returns content matching selected genres/movies.

---

### TC-09 — API returns 400 for fewer than 3 genre IDs

**Type:** Negative | **Priority:** High | **Layer:** API | **Group:** api

**Steps:**
1. Send `POST /v1/profile/{profile_id}/vod-preferences` with body:
```json
{
  "genre_ids": [1, 5],
  "movie_ids": [101, 202, 303, 404, 505]
}
```

**Expected Result:** Response `400 Bad Request` with error message.

---

### TC-10 — API returns 400 for empty genre and movie lists

**Type:** Negative (Boundary) | **Priority:** High | **Layer:** API | **Group:** api

**Steps:**
1. Send `POST /v1/profile/{profile_id}/vod-preferences` with body:
```json
{
  "genre_ids": [],
  "movie_ids": []
}
```

**Expected Result:** Response `400 Bad Request`.

---

### TC-11 — API returns 404 for non-existent profile_id

**Type:** Negative | **Priority:** High | **Layer:** API | **Group:** api

**Steps:**
1. Send `POST /v1/profile/99999999/vod-preferences` with valid body.

**Expected Result:** Response `404 Not Found`.

---

### TC-12 — API returns 401 when request is unauthenticated

**Type:** Negative | **Priority:** High | **Layer:** API / Security | **Group:** api

**Steps:**
1. Send `POST /v1/profile/{profile_id}/vod-preferences` without auth token.

**Expected Result:** Response `401 Unauthorized`.

---

### TC-13 — User skips onboarding — profile receives default recommendations

**Type:** Positive | **Priority:** High | **Layer:** E2E / API | **Group:** api

**Steps:**
1. Click "Skip" button.
2. Check the recommendations endpoint for this profile.

**Expected Result:**
- Onboarding screen closes.
- `GET /v1/profile/{profile_id}/recommendations` returns default content.
- Profile is marked as "onboarding completed".

---

### TC-14 — Skipping onboarding does NOT call POST /vod-preferences

**Type:** Negative | **Priority:** Medium | **Layer:** API | **Group:** api

**Steps:**
1. Click "Skip".
2. Check server logs or intercept network calls.

**Expected Result:** `POST /v1/profile/{profile_id}/vod-preferences` is NOT called.

---

### TC-15 — Duplicate POST /vod-preferences for same profile is handled correctly

**Type:** Boundary | **Priority:** Medium | **Layer:** API | **Group:** api

**Steps:**
1. Send `POST /v1/profile/{profile_id}/vod-preferences` a second time with different data.

**Expected Result:** Either `200 OK` (update) or `409 Conflict` — consistent behaviour.

---

### TC-16 — Recommendations update after preferences are saved

**Type:** Positive | **Priority:** Critical | **Layer:** API / Integration | **Group:** api

**Steps:**
1. Call `GET /v1/profile/{profile_id}/recommendations` — note results.
2. Call `POST /v1/profile/{profile_id}/vod-preferences` with valid genres and movies.
3. Call `GET /v1/profile/{profile_id}/recommendations` again.

**Expected Result:** Recommendations in Step 3 differ from Step 1 and reflect the submitted preferences.

---

### TC-17 — API returns 400 for invalid genre IDs

**Type:** Negative | **Priority:** Medium | **Layer:** API | **Group:** api

**Steps:**
1. Send `POST /v1/profile/{profile_id}/vod-preferences` with non-existent genre IDs.

**Expected Result:** Response `400 Bad Request` or `422 Unprocessable Entity`.

---

### TC-18 — API handles request with missing movie_ids field

**Type:** Boundary | **Priority:** Medium | **Layer:** API | **Group:** api

**Steps:**
1. Send `POST /v1/profile/{profile_id}/vod-preferences` with only `genre_ids`, omitting `movie_ids`.

**Expected Result:** Documented behaviour — either saves genres only or returns `400`.

---

## 🟢 UI TEST CASES
> Added manually — AI initially missed the UI test layer requirement from the task description.
> Automated using Playwright for Java.

### TC-UI-01 — "Next" button is disabled when fewer than 3 genres selected

**Type:** Negative (Boundary) | **Priority:** High | **Layer:** UI | **Group:** ui

**Preconditions:** Onboarding screen is visible; Step 1 (genre selection).

**Steps:**
1. Navigate to onboarding screen.
2. Select 2 genres.
3. Observe "Next" button state.

**Expected Result:** "Next" button is disabled/inactive.

**Automated:** `VodOnboardingUiTest.testNextButtonDisabledWithLessThanThreeGenres`

---

### TC-UI-02 — "Next" button activates when exactly 3 genres are selected

**Type:** Positive (Boundary) | **Priority:** High | **Layer:** UI | **Group:** ui

**Preconditions:** Onboarding screen is visible; Step 1.

**Steps:**
1. Navigate to onboarding screen.
2. Select exactly 3 genres.
3. Observe "Next" button state.

**Expected Result:** "Next" button becomes active/enabled.

**Automated:** `VodOnboardingUiTest.testNextButtonEnabledWithThreeGenres`

---

## Summary

| ID | Scenario | Type | Layer | Group | Priority |
|----|----------|------|-------|-------|----------|
| TC-01 | Onboarding appears once after profile creation | Positive | E2E | api | High |
| TC-02 | Onboarding not shown on second visit | Negative | E2E | api | High |
| TC-03 | Next disabled with < 3 genres | Negative | E2E/UI Logic | api | High |
| TC-04 | Next active with exactly 3 genres | Boundary | E2E/UI Logic | api | High |
| TC-05 | Next active with > 3 genres | Positive | E2E/UI Logic | api | Medium |
| TC-06 | Step 2 shows movies by genres | Positive | E2E/API | api | High |
| TC-07 | User selects 5 movies and submits | Positive | E2E | api | High |
| TC-08 | API saves genre & movie IDs correctly | Positive | API | api | Critical |
| TC-09 | API 400 for < 3 genres | Negative | API | api | High |
| TC-10 | API 400 for empty body | Negative | API | api | High |
| TC-11 | API 404 for invalid profile_id | Negative | API | api | High |
| TC-12 | API 401 without auth token | Negative | API/Security | api | High |
| TC-13 | Skip → default recommendations | Positive | E2E/API | api | High |
| TC-14 | Skip → POST not called | Negative | API | api | Medium |
| TC-15 | Duplicate POST handled correctly | Boundary | API | api | Medium |
| TC-16 | Recommendations update after preferences saved | Positive | API/Integration | api | Critical |
| TC-17 | API 400 for invalid genre IDs | Negative | API | api | Medium |
| TC-18 | API handles missing movie_ids field | Boundary | API | api | Medium |
| TC-UI-01 | Next button disabled with < 3 genres (UI) | Negative | UI | ui | High |
| TC-UI-02 | Next button enabled with exactly 3 genres (UI) | Positive | UI | ui | High |
