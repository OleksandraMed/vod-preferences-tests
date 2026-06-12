# AI-STRATEGY.md — AI-Powered Test Generation Strategy

## Overview
This document describes a strategy for automatically generating regression tests
for internal advertising campaigns (start screen popups) using an AI-first approach.

---

## Problem Statement
The platform displays various promotional start screen popups to users:
- New movie/series recommendations
- Partner integrations
- Subscription discount offers
- Creative ad banners

Each campaign type has unique business logic, API contracts, and validation rules.
Manual test creation for each campaign is time-consuming and error-prone.

---

## Proposed AI-First Strategy

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

### Step 5 — AI-Powered Bug Report Generation
When a test fails in CI, AI automatically creates a Jira bug report:

**Trigger:** Test failure in pipeline

**AI Prompt:**
```
Given this test failure:
- Test name: {test_name}
- Expected: {expected_result}
- Actual: {actual_result}
- Stack trace: {stack_trace}

Generate a Jira bug report with:
- Summary (one line)
- Environment
- Steps to reproduce
- Expected vs Actual result
- Severity assessment
```

**Output:** Jira ticket created automatically via Jira REST API.

---

## Coverage Targets

| Campaign Type | Test Cases | AI Generated | Manual Review |
|---------------|------------|--------------|---------------|
| Movie recommendation | ~12 | 90% | 10% |
| Series recommendation | ~12 | 90% | 10% |
| Partner integration | ~15 | 85% | 15% |
| Subscription discount | ~10 | 90% | 10% |
| Creative ad banner | ~10 | 85% | 15% |

---

## Benefits
- **Speed:** New campaign test suite generated in minutes, not days
- **Consistency:** All campaigns tested against same quality standards
- **Scalability:** Adding new campaign type = updating spec + running prompt
- **Traceability:** Every test linked to campaign spec and business requirement

---

## Risks & Mitigations

| Risk | Mitigation |
|------|------------|
| AI generates incorrect assertions | Human review step before merge |
| AI misses edge cases | Mandatory boundary test gate |
| Generated code doesn't compile | Automated compile check in pipeline |
| Prompt quality degrades | PROMPTS.md maintained and versioned |
