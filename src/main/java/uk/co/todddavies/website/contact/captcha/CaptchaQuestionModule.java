package uk.co.todddavies.website.contact.captcha;

import com.google.common.collect.ImmutableList;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import java.util.Random;

public class CaptchaQuestionModule extends AbstractModule {

  // TODO(td): Load these from Datastore or something.
  private static final ImmutableList<CaptchaQuestion> QUESTIONS = ImmutableList.of(
      CaptchaQuestion.create("What is my first name?", "Todd"),
      CaptchaQuestion.create("The planet that we live on is called...?", "Earth"));

  private final Random random;
  
  public CaptchaQuestionModule() {
    random = new Random();
  }
  
  @Override
  protected void configure() { /* Nothing to configure here. */ }

  @Provides
  CaptchaQuestion provideRandomQuestion() {
    return QUESTIONS.get(random.nextInt(QUESTIONS.size()));
  }
}
