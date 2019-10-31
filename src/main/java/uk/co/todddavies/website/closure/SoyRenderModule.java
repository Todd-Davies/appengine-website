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
    SoyFileSet.Builder fileSet = SoyFileSet.builder();
    ServletContext context = getServletContext();
    for (String path : context.getResourcePaths("/WEB-INF/templates/")) {
      fileSet.add(getServletContext().getResource(path));
    }
    for (String path : context.getResourcePaths("/WEB-INF/blog/")) {
      fileSet.add(getServletContext().getResource(path));
    }
    return fileSet;
  }
}
