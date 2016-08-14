package uk.co.todddavies.website;

import uk.co.todddavies.website.contact.ContactServletModule;
import uk.co.todddavies.website.notes.NotesServletModule;
import uk.co.todddavies.website.pages.PagesServletModule;
import uk.co.todddavies.website.sslverification.SslVerificationServletModule;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;

public final class ApiServletContextListener  extends GuiceServletContextListener {

  private static final String API_PATH = "/api/";
  
  @Override protected Injector getInjector() {
    return Guice.createInjector(
        RedirectModule.create(),
        new JsonObjectWriterModule(),
        new PagesServletModule(API_PATH),
        // Used when verifying ownership of the domain for https renewal
        new SslVerificationServletModule(false),
        ContactServletModule.create(API_PATH),
        NotesServletModule.create(API_PATH));
  }
}