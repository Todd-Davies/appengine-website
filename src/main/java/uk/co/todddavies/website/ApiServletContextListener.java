package uk.co.todddavies.website;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import uk.co.todddavies.website.blog.BlogModule;
import uk.co.todddavies.website.cache.MemcacheModule;
import uk.co.todddavies.website.closure.TemplateServletModule;
import uk.co.todddavies.website.notes.NotesServletModule;
import uk.co.todddavies.website.notes.data.NotesDatastoreModule;
import uk.co.todddavies.website.sslverification.SslVerificationServletModule;
import uk.co.todddavies.website.taskqueue.TaskQueueModule;

public final class ApiServletContextListener  extends GuiceServletContextListener {

  private static final String API_PATH = "/api/";
  
  @Override protected Injector getInjector() {
    return Guice.createInjector(
        RedirectModule.create(),
        new JsonObjectWriterModule(),
        // Used when verifying ownership of the domain for https renewal
        new SslVerificationServletModule(false),
        NotesServletModule.create(API_PATH),
        TemplateServletModule.create(),
        BlogModule.create(),
        // Global notes binding modules to avoid multiple bindings
        new MemcacheModule(),
        new NotesDatastoreModule(),
        new TaskQueueModule());
  }
}