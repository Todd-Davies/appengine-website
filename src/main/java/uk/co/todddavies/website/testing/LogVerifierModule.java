package uk.co.todddavies.website.testing;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.MapBinder;

public class LogVerifierModule extends AbstractModule {

  private final Class<?>[] targetClasses;
  
  private LogVerifierModule(Class<?>... targetClasses) {
    this.targetClasses = targetClasses;
  }
  
  public static LogVerifierModule create(Class<?>... targetClasses) {
    return new LogVerifierModule(targetClasses);
  }
  
  @Override
  protected void configure() {
    MapBinder<? super Class<?>, LogVerifier> logVerifierBinder =
        MapBinder.newMapBinder(binder(), Class.class, LogVerifier.class);
    for (Class<?> targetClass : targetClasses) {
      logVerifierBinder.addBinding(targetClass).toInstance(new LogVerifier(targetClass));
    }
  }
}
