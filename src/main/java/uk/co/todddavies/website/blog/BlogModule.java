package uk.co.todddavies.website.blog;

import com.google.inject.AbstractModule;
import com.google.inject.servlet.ServletModule;

public class BlogModule extends ServletModule {

  private BlogModule() {}

  public static AbstractModule create() {
    return new BlogModule();
  }

  @Override
  protected void configureServlets() {
    serve("/blog/").with(BlogHomeServletModule.class);
    serve("/blog/*").with(BlogPostServletModule.class);
  }
}
