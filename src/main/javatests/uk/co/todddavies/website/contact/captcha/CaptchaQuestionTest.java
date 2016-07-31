package uk.co.todddavies.website.contact.captcha;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class CaptchaQuestionTest {
  
  @Test
  public void testXorSuperposition() {
    CaptchaQuestion testQuestion = CaptchaQuestion.create("", "test");
    String expectedAnswer = "0XX-0XX-0XX-0XX";
    String actualAnswer = testQuestion.encryptSecret("test");
    assertEquals(expectedAnswer, actualAnswer);
  }
  
  @Test
  public void testXorHighNumbers() {
    CaptchaQuestion testQuestion = CaptchaQuestion.create("", "0123");
    String expectedAnswer = "106-107-104-105";
    String actualAnswer = testQuestion.encryptSecret("ZZZZ");
    assertEquals(expectedAnswer, actualAnswer);
  }
  
  @Test
  public void testXorRepeatKey() {
    CaptchaQuestion testQuestion = CaptchaQuestion.create("", "ab");
    String expectedAnswer = "0XX-3XX-0XX-3XX-0XX";
    String actualAnswer = testQuestion.encryptSecret("aaaaa");
    assertEquals(expectedAnswer, actualAnswer);
  }
  
  @Test
  public void testXorNoInput() {
    CaptchaQuestion testQuestion = CaptchaQuestion.create("", "ab");
    String expectedAnswer = "";
    String actualAnswer = testQuestion.encryptSecret("");
    assertEquals(expectedAnswer, actualAnswer);
  }
  
  @Test
  public void testXorNullInput() {
    CaptchaQuestion testQuestion = CaptchaQuestion.create("", "ab");
    String expectedAnswer = "";
    String actualAnswer = testQuestion.encryptSecret(null);
    assertEquals(expectedAnswer, actualAnswer);
  }
  
  @Test
  public void testCaptchaNullInput() {
    try {
      CaptchaQuestion.create("", null);
    } catch (RuntimeException ex) {
      assertEquals("Answer cannot be null", ex.getMessage());
    }
    try {
      CaptchaQuestion.create(null, "");
    } catch (RuntimeException ex) {
      assertEquals("Question cannot be null", ex.getMessage());
    }
  }
}
