package uk.co.todddavies.website.contact.captcha;

import org.apache.commons.codec.binary.Hex;;

public final class CaptchaQuestion {

  static CaptchaQuestion create(String question, String answer) {
    return new CaptchaQuestion(question, answer.toLowerCase());
  }
  
  private final String question, answer;
  
  private CaptchaQuestion(String question, String answer) {
    this.question = question;
    this.answer = answer;
  }
  
  public String getQuestion() {
    return question;
  }
  
  public String encryptSecret(String secret) {
    final byte[] key = answer.getBytes();
    byte[] out = secret.getBytes();
    for (int i = 0; i < out.length; i++) {
        out[i] ^= (byte) key[i%key.length];
    }
    return new String(Hex.encodeHexString(out));
  }
}
