package uk.co.todddavies.website;

import uk.co.todddavies.website.contact.ContactServletModule;
import uk.co.todddavies.website.notes.NotesServletModule;
import uk.co.todddavies.website.pages.PagesServletModule;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;

public class ApiServletContextListener  extends GuiceServletContextListener {

  @Override protected Injector getInjector() {
    return Guice.createInjector(
        new JsonObjectWriterModule(),
        new PagesServletModule(),
        ContactServletModule.create(),
        NotesServletModule.create());
  }
}