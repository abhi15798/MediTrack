package com.airtribe.meditrack.tests;

import com.airtribe.meditrack.util.AIHelper;

import java.lang.reflect.Method;
import java.util.List;

public class AIHelperTest {

    public static void main(String[] args) {
        AIHelperTest runner = new AIHelperTest();

        System.out.println("Starting AIHelper manual tests...");
        runner.testGetInstance();
        runner.testClassifySpecialtyWithoutApiKey();
        runner.testGetJsonPayload();
        runner.testParseGeminiContent();

        System.out.println("All AIHelper manual tests passed.");
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    public void testGetInstance() {
        AIHelper first = AIHelper.getInstance();
        AIHelper second = AIHelper.getInstance();

        assertTrue(first == second, "getInstance should return the same singleton instance");
        System.out.println("PASS: testGetInstance");
    }

    public void testClassifySpecialtyWithoutApiKey() {
        AIHelper helper = AIHelper.getInstance();
        List<String> specialties = List.of("General Physician", "Cardiology", "Dermatology");

        String result = helper.classifySpecialty("Chest pain and shortness of breath", specialties);

        assertTrue("General Physician".equals(result),
                "classifySpecialty should fall back to General Physician when the API key is missing");
        System.out.println("PASS: testClassifySpecialtyWithoutApiKey");
    }

    public void testGetJsonPayload() {
        try {
            Method method = AIHelper.class.getDeclaredMethod("getJsonPayload", String.class, String.class);
            method.setAccessible(true);
            String payload = (String) method.invoke(null, "Chest pain", "General Physician, Cardiology");

            assertTrue(payload.contains("Chest pain"), "getJsonPayload should include the human description");
            assertTrue(payload.contains("General Physician, Cardiology"), "getJsonPayload should include the specialties list");
            assertTrue(payload.contains("temperature\": 0.0"), "getJsonPayload should set a zero temperature for deterministic output");
            System.out.println("PASS: testGetJsonPayload");
        } catch (Exception e) {
            throw new AssertionError("Reflection call to getJsonPayload failed", e);
        }
    }

    public void testParseGeminiContent() {
        try {
            AIHelper helper = AIHelper.getInstance();
            Method method = AIHelper.class.getDeclaredMethod("parseGeminiContent", String.class);
            method.setAccessible(true);
            String parsed = (String) method.invoke(helper, "{\"text\":\"Cardiology\"}");

            assertTrue("Cardiology".equals(parsed), "parseGeminiContent should extract the specialty name from JSON");
            System.out.println("PASS: testParseGeminiContent");
        } catch (Exception e) {
            throw new AssertionError("Reflection call to parseGeminiContent failed", e);
        }
    }
}
