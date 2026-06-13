package com.vod.tests;

import com.microsoft.playwright.*;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

public class BaseUiTest {

    protected Playwright playwright;
    protected Browser browser;
    protected Page page;

    @BeforeClass
    public void setup() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch();
        page = browser.newPage();
    }

    @AfterClass
    public void teardown() {
        browser.close();
        playwright.close();
    }
}

