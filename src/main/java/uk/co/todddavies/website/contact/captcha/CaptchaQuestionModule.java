package uk.co.todddavies.website.contact.captcha;

import com.google.common.collect.ImmutableList;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import java.util.Random;

public class CaptchaQuestionModule extends AbstractModule {

  private static final ImmutableList<CaptchaQuestion> QUESTIONS = ImmutableList.of(
      CaptchaQuestion.create("What is my first name?", "Todd"),
      CaptchaQuestion.create("The planet that we live on is called...?", "Earth"));

  private final Random random;
  
  public CaptchaQuestionModule() {
    random = new Random();
  }
  
  @Override
  protected void configure() {}

  @Provides
  CaptchaQuestion provideRandomQuestion() {
    if (QUESTIONS.isEmpty()) {
      throw new RuntimeException("No questions found!");
    } else {
      return QUESTIONS.get(random.nextInt(QUESTIONS.size()));
    }
  }
}
