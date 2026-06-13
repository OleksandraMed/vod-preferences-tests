# AI-STRATEGY.md — AI-Powered Test Generation & Analysis Strategy

## Overview
This document describes:
1. A strategy for automatically generating regression tests for internal advertising campaigns (start screen popups) using an AI-first approach.
2. A strategy for AI-powered test results analysis and automatic Jira bug report creation.

---

## Part 1: AI-Powered Test Generation for Advertising Campaigns

### Problem Statement
The platform displays various promotional start screen popups to users:
- New movie/series recommendations
- Partner integrations
- Subscription discount offers
- Creative ad banners

Each campaign type has unique business logic, API contracts, and validation rules.
Manual test creation for each campaign is time-consuming and error-prone.

---

### Step 1 — Single Source of Truth
Every campaign is described in a structured specification (JSON or YAML):

```json
{
  "campaign_type": "subscription_discount",
  "trigger": "profile_open",
  "show_once": true,
  "api_endpoint": "POST /v1/profile/{id}/campaigns/interaction",
  "required_fields": ["campaign_id", "action"],
  "valid_actions": ["accepted", "dismissed"],
  "expected_response": 200
}
```

This spec becomes the INPUT to the AI prompt.

---

### Step 2 — AI Prompt Template
For each new campaign, the following prompt template is used:

```
You are a QA Automation Engineer.
Given this API specification: {campaign_spec}
Generate regression test cases covering:
- Positive: valid interaction saved correctly
- Negative: missing fields, invalid actions, wrong campaign_id
- Boundary: duplicate interactions, expired campaigns
- Security: unauthenticated requests

Output format: Java + REST Assured test methods with TestNG annotations.
Base class: BaseApiTest. Use constants from CampaignConfig.
```

---

### Step 3 — AI Output Pipeline

```
Campaign Spec (JSON)
       ↓
AI Prompt Template
       ↓
Generated Test Code (Java)
       ↓
Automated Review (compile check + static analysis)
       ↓
Pull Request → CI Pipeline
       ↓
Test Execution + Allure Report
```

---

### Step 4 — Quality Gates
Before merging AI-generated tests, automated checks verify:
- Code compiles without errors
- Test method names follow naming convention
- At least 1 positive, 1 negative, 1 boundary test per endpoint
- No hardcoded credentials

---

### Coverage Targets

| Campaign Type | Test Cases | AI Generated | Manual Review |
|---------------|------------|--------------|---------------|
| Movie recommendation | ~12 | 90% | 10% |
| Series recommendation | ~12 | 90% | 10% |
| Partner integration | ~15 | 85% | 15% |
| Subscription discount | ~10 | 90% | 10% |
| Creative ad banner | ~10 | 85% | 15% |

---

### Benefits
- **Speed:** New campaign test suite generated in minutes, not days
- **Consistency:** All campaigns tested against same quality standards
- **Scalability:** Adding new campaign type = updating spec + running prompt
- **Traceability:** Every test linked to campaign spec and business requirement

---

### Risks & Mitigations

| Risk | Mitigation |
|------|------------|
| AI generates incorrect assertions | Human review step before merge |
| AI misses edge cases | Mandatory boundary test gate |
| Generated code doesn't compile | Automated compile check in pipeline |
| Prompt quality degrades | PROMPTS.md maintained and versioned |

---

## Part 2: AI-Powered Test Results Analysis & Jira Bug Reporting

### Problem Statement
After test runs in CI/CD pipeline, QA engineers manually:
1. Review logs
2. Analyze what failed and why
3. Create bug reports in Jira

This takes 15-30 minutes per bug. With many tests and frequent releases — this is a critical time drain.

---

### Solution: AI-Powered Bug Report Generation

#### How it works

```
Test fails in CI
       ↓
Collect failure data (test name, expected, actual, stack trace)
       ↓
Send to AI with prompt template
       ↓
AI generates structured Jira bug report
       ↓
Jira REST API creates ticket automatically
       ↓
Team gets notification in Slack
```

---

#### Step 1 — Collect Test Failure Data

After a test fails in CI, the following data is collected:

```json
{
  "test_name": "testSavePreferencesWithTwoGenres",
  "test_class": "VodPreferencesApiTest",
  "expected": "HTTP 400 Bad Request",
  "actual": "HTTP 200 OK",
  "stack_trace": "java.lang.AssertionError: expected [400] but found [200]",
  "environment": "staging",
  "timestamp": "2026-06-13T15:33:48",
  "build_id": "CI-1234"
}
```

---

#### Step 2 — AI Prompt for Bug Report Generation

```
You are a QA Engineer writing a Jira bug report.
Given this test failure data: {failure_data}

Generate a Jira bug report in JSON format with these fields:
- summary: one-line description of the bug
- description: detailed description with steps to reproduce
- expected_result: what should happen
- actual_result: what actually happened
- severity: Critical/High/Medium/Low
- priority: P1/P2/P3/P4
- labels: ["regression", "api", "automated"]
- component: API or UI

Return ONLY valid JSON, no extra text.
```

---

#### Step 3 — Example AI Output

```json
{
  "summary": "[API] POST /vod-preferences returns 200 instead of 400 for 2 genres",
  "description": "Automated test detected that the API accepts requests with fewer than 3 genres without returning an error.\n\nSteps to reproduce:\n1. Send POST /v1/profile/{id}/vod-preferences with genre_ids: [1, 5]\n2. Observe response status code\n\nBuild: CI-1234\nEnvironment: staging\nTimestamp: 2026-06-13T15:33:48",
  "expected_result": "HTTP 400 Bad Request with error 'At least 3 genres required'",
  "actual_result": "HTTP 200 OK — request accepted with only 2 genres",
  "severity": "High",
  "priority": "P2",
  "labels": ["regression", "api", "automated", "vod-preferences"],
  "component": "API"
}
```

---

#### Step 4 — Auto-create Jira Ticket

```java
// After AI generates the bug report JSON
public void createJiraBugReport(String aiGeneratedJson) {
    given()
        .header("Authorization", "Bearer " + JIRA_TOKEN)
        .contentType(ContentType.JSON)
        .body(aiGeneratedJson)
        .when()
        .post("https://your-company.atlassian.net/rest/api/3/issue")
        .then()
        .statusCode(201);
}
```

---

#### Step 5 — Slack Notification

After Jira ticket is created — automatic message to team Slack channel:

```
🔴 Test Failed: testSavePreferencesWithTwoGenres
📋 Jira: VOD-1234 created automatically
🌍 Environment: staging
⏰ Time: 2026-06-13 15:33
```

---

### Integration with Current Project

In this project (`vod-preferences-tests`) the integration would look like:

```
VodPreferencesApiTest fails
       ↓
TestNG Listener catches failure
       ↓
AI generates bug report
       ↓
Jira ticket created: VOD-XXXX
       ↓
Slack notification sent
```

---

### Time Savings

| Metric | Without AI | With AI |
|--------|-----------|---------|
| Time to create bug report | 15-30 min | 30 sec |
| Human error in description | Present | Minimal |
| Coverage of all failures | Depends on QA | 100% automated |
| Report format consistency | Varies | Always consistent |

---

## Conclusion

Combining AI-powered test generation (Part 1) with AI-powered bug reporting (Part 2)
creates a fully automated QA pipeline where:
- New campaign → tests generated automatically
- Test fails → bug report created automatically
- Team focuses on fixing bugs, not documenting them
