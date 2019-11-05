package uk.co.todddavies.website.closure;

import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.servlet.ServletModule;
import com.google.template.soy.SoyFileSet;
import com.google.template.soy.jbcsrc.api.SoySauce;

import javax.servlet.ServletContext;
import java.net.MalformedURLException;

final class SoyRenderModule extends ServletModule {

  @Singleton
  @Provides
  SoySauce provideSoySauce() throws MalformedURLException {
    return getFileSetBuilder().build().compileTemplates();
  }

  private SoyFileSet.Builder getFileSetBuilder() throws MalformedURLException {
    return findTemplates(getServletContext(), SoyFileSet.builder(), "/WEB-INF/templates");
  }

  private SoyFileSet.Builder findTemplates(ServletContext context, SoyFileSet.Builder builder, String path)
      throws MalformedURLException {
    for (String item : context.getResourcePaths(path)) {
      if (item.endsWith("/")) {
        findTemplates(context, builder, item);
      } else {
        builder.add(getServletContext().getResource(item));
      }
    }
    return builder;
  }
}
