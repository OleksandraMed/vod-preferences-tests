# PROMPTS.md — AI Interaction Log

## Overview
This document logs all prompts used to generate test artifacts for the VOD onboarding flow,
including cases where AI made mistakes and how they were corrected.

---

## PROMPT #1 — Test Case Generation

**Goal:** Generate regression test cases for the VOD onboarding survey flow.

**Prompt given to AI:**
> "You are a QA Engineer. Based on the following User Story, generate a comprehensive
> set of test cases for regression testing. Include positive, negative, and boundary
> scenarios. Focus on API-level and E2E testing, NOT UI positioning or styling.
> Requirements:
> - Onboarding screen appears only once after new profile creation
> - User must select at least 3 genres for Next button to activate
> - Second step: select 5 movies based on chosen genres
> - User can skip survey — profile gets default recommendations
> - API: POST /v1/profile/{profile_id}/vod-preferences saves genre and movie IDs
> - Data must persist in DB and affect recommendations endpoint"

**AI Output:** 18 test cases covering positive, negative, and boundary scenarios.

**What AI did well:**
- Covered all 6 requirements from the User Story
- Independently added security scenario (TC-12: 401 Unauthorized)
- Added integration test (TC-16: recommendations update after preferences saved)
- Correctly identified boundary case of exactly 3 genres (TC-04)

**What AI missed / needed correction:**
- Did not include a test for duplicate POST request (TC-15) — added manually
- Did not explicitly test that skip action does NOT call the API (TC-14) — added manually
- Initial format had no summary table — requested as follow-up prompt
- **AI did not generate UI test cases at all** — focused only on API/E2E layer. Reviewer (human) pointed out that the task explicitly requires UI-level tests for the "Next" button logic. UI test cases TC-UI-01 and TC-UI-02 were added manually after this correction.

**Follow-up prompt:**
> "Add a summary table at the end with all test case IDs, titles, types, layers and priority"

---

## PROMPT #2 — Project Structure & Test Code Generation

**Goal:** Generate Java + REST Assured automation project structure.

**Prompt given to AI:**
> "Generate a Maven project structure for API test automation using Java, REST Assured,
> TestNG and Allure. Include:
> - ApiConfig class with base URL and auth token constants
> - VodPreferencesRequest model class
> - Test class covering TC-08, TC-09, TC-10, TC-11, TC-12, TC-16
> - testng.xml for test suite configuration"

**AI Output:** Full project structure with pom.xml, config, model and test classes.

**What AI did well:**
- Generated clean REST Assured syntax with given/when/then pattern
- Correctly used path parameters for profile_id
- Added @BeforeClass setup method
- Generated proper TestNG annotations

**What AI missed / needed correction:**
- AI initially placed testng.xml in project root — corrected to src/test/resources/
- AI suggested adding suiteXmlFiles configuration to surefire plugin in pom.xml — configuration was added but caused NullPointerException when running tests. Fix: removed suiteXmlFiles configuration manually, which resolved the issue
- AI did not add .gitignore — created manually to exclude target/ and allure-results/
- **AI generated tests only starting from TC-08** — completely missed TC-01, TC-02, TC-05, TC-06, TC-07. Reviewer (human) identified that these test cases were in TEST-CASES.md but had no corresponding automated tests. AI was prompted to add the missing tests.
- **AI did not propose UI tests at all** — only generated API tests. Reviewer (human) pointed out that the task stack is "Java + Playwright" and UI-level button logic must be covered. AI was prompted to add Playwright for Java.

**Follow-up prompt #1:**
> "The task requires Java + Playwright for UI tests. Add Playwright dependency to pom.xml
> and generate UI tests for the Next button logic (disabled < 3 genres, enabled = 3 genres,
> enabled > 3 genres). Use BaseUiTest pattern with setup/teardown separated from test class."

**Follow-up prompt #2:**
> "You missed TC-01, TC-02, TC-06, TC-07 automation. TC-01 and TC-02 should check
> onboarding_completed flag via GET /v1/profile/{id}. TC-06 should verify movies
> endpoint filters by genre. TC-07 is already covered by TC-08. Please add missing tests."

**AI Output after corrections:** Added BaseUiTest, VodOnboardingUiTest with TC-03/TC-04/TC-05,
and added TC-01, TC-02, TC-06 to VodPreferencesApiTest.

---

## PROMPT #3 — README Generation

**Goal:** Generate project README for GitHub repository.

**Prompt given to AI:**
> "Generate a README.md for a Java Maven test automation project for VOD onboarding API.
> Include: project description, tech stack, project structure, how to run tests,
> test coverage overview. Include both API (REST Assured) and UI (Playwright) test layers."

**AI Output:** Complete README with all requested sections.

**What AI did well:**
- Structured README clearly with all required sections
- Included correct mvn test command with group filtering
- Listed all technologies accurately

**What AI missed / needed correction:**
- Initial README did not include Playwright or UI tests — updated after UI layer was added

---

## Summary: AI Accuracy Assessment

| Artifact | AI Generated | Manual Corrections |
|----------|--------------|-------------------|
| Test cases | 16/20 (80%) | 2 API cases + 2 UI cases added manually |
| pom.xml | 85% | suiteXmlFiles config added then removed to fix NullPointerException |
| API test code | 70% | TC-01, TC-02, TC-06 missed — added after reviewer identified gap |
| UI test code | 0% initially → 90% after prompt | AI completely missed UI layer — added after reviewer correction |
| Project structure | 85% | testng.xml location fix, groups added manually |
| README | 95% | Updated to include UI layer |

**Key learnings:**
- AI focused only on direct API endpoint tests and missed E2E flow scenarios (TC-01, TC-02, TC-06)
- AI completely missed UI test requirement from the task description
- AI missed TC-05 (Next button with > 3 genres) in UI tests
- Human reviewer identified all gaps and corrected via follow-up prompts

**Overall AI contribution: ~82% of all artifacts generated by AI**
