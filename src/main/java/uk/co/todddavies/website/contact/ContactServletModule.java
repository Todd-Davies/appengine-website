package uk.co.todddavies.website.contact;

import uk.co.todddavies.website.contact.Annotations.EmailAddress;
import uk.co.todddavies.website.contact.captcha.CaptchaQuestionModule;

import com.google.inject.AbstractModule;
import com.google.inject.servlet.ServletModule;

public final class ContactServletModule extends ServletModule {
  
  // TODO(td): Get this with a flag
  private static final String EMAIL = "todd434@gmail.com";
  
  private final String apiPath;
  
  private ContactServletModule(String apiPath) {
    this.apiPath = apiPath;
  }
  
  public static final AbstractModule create(final String apiPath) {
    return new AbstractModule() {
      @Override
      protected void configure() {
        install(new ContactServletModule(apiPath));
        install(new CaptchaQuestionModule());
      }
    };
  }
  
  @Override
  protected void configureServlets() {
    serve(apiPath + "contact").with(ContactApiServlet.class);
    bind(String.class).annotatedWith(EmailAddress.class).toInstance(EMAIL);
  }  
}
