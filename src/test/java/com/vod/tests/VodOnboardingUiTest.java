package com.vod.tests;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import org.testng.annotations.Test;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class VodOnboardingUiTest extends BaseUiTest {

    // TC-UI-01 / TC-03: Next button disabled when fewer than 3 genres selected
    @Test(description = "Next button is disabled when fewer than 3 genres selected", groups = "ui")
    public void testNextButtonDisabledWithLessThanThreeGenres() {
        page.navigate("https://example.com/onboarding");
        page.getByTestId("genre-action").click();
        page.getByTestId("genre-comedy").click();

        Locator nextButton = page.getByRole(AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Далі"));

        assertFalse(nextButton.isEnabled(),
                "Next button should be disabled when fewer than 3 genres are selected");
    }

    // TC-UI-02 / TC-04: Next button activates when exactly 3 genres selected
    @Test(description = "Next button is enabled when exactly 3 genres selected", groups = "ui")
    public void testNextButtonEnabledWithThreeGenres() {
        page.navigate("https://example.com/onboarding");
        page.getByTestId("genre-action").click();
        page.getByTestId("genre-comedy").click();
        page.getByTestId("genre-drama").click();

        Locator nextButton = page.getByRole(AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Далі"));

        assertTrue(nextButton.isEnabled(),
                "Next button should be enabled when exactly 3 genres are selected");
    }

    // TC-05: Next button remains active when more than 3 genres selected
    @Test(description = "Next button remains enabled when more than 3 genres selected", groups = "ui")
    public void testNextButtonRemainsEnabledWithMoreThanThreeGenres() {
        page.navigate("https://example.com/onboarding");
        page.getByTestId("genre-action").click();
        page.getByTestId("genre-comedy").click();
        page.getByTestId("genre-drama").click();
        page.getByTestId("genre-thriller").click();

        Locator nextButton = page.getByRole(AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Далі"));

        assertTrue(nextButton.isEnabled(),
                "Next button should remain enabled when more than 3 genres are selected");
    }
}