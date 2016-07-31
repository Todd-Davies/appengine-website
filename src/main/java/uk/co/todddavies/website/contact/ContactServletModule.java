package uk.co.todddavies.website.contact;

import uk.co.todddavies.website.contact.Annotations.EmailAddress;
import uk.co.todddavies.website.contact.captcha.CaptchaQuestionModule;

import com.google.inject.AbstractModule;
import com.google.inject.servlet.ServletModule;

public class ContactServletModule extends ServletModule {

  // TODO(td): Get this with a flag
  private static final String EMAIL = "todd434@gmail.com";
  
  public static final AbstractModule create() {
    return new AbstractModule() {
      @Override
      protected void configure() {
        install(new ContactServletModule());
        install(new CaptchaQuestionModule());
      }
    };
  }
  
  private ContactServletModule() {}
  
  @Override
  protected void configureServlets() {
    serve("/api/contact").with(ContactApiServlet.class);
    bind(String.class).annotatedWith(EmailAddress.class).toInstance(EMAIL);
  }  
}
