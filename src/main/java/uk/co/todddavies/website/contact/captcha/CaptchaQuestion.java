package uk.co.todddavies.website.contact.captcha;

import com.google.api.client.util.Preconditions;


public final class CaptchaQuestion {
  
  private final String question;
  private final String answer;
  
  static CaptchaQuestion create(String question, String answer) {
    Preconditions.checkNotNull(question, "Question cannot be null");
    Preconditions.checkNotNull(answer, "Answer cannot be null");
    return new CaptchaQuestion(question, answer.toLowerCase());
  }
  
  private CaptchaQuestion(String question, String answer) {
    this.question = question;
    this.answer = answer;
  }
  
  public String getQuestion() {
    return question;
  }
  
  public String encryptSecret(String secret) {
    if (secret == null) {
      return "";
    } else {
      StringBuilder output = new StringBuilder();
      for (int i = 0; i < secret.length(); i++) {
        output.append(i == 0 ? "" : "-");
        int encryptedChar = answer.charAt(i % answer.length()) ^ secret.charAt(i);
        output.append(encryptedChar);
        output.append(encryptedChar < 100 ? encryptedChar < 10 ? "XX" : "X" : "");
      }
      return output.toString();
    }
  }
}
