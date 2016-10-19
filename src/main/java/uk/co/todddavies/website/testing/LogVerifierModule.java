package uk.co.todddavies.website.testing;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.MapBinder;

/**
 * Module for providing {@code LogVerifier}s.
 */
public class LogVerifierModule extends AbstractModule {

  private final Class<?>[] targetClasses;
  private final boolean suppressLogOutput;
  
  private LogVerifierModule(boolean suppressLogOutput, Class<?>... targetClasses) {
    this.targetClasses = targetClasses;
    this.suppressLogOutput = suppressLogOutput;
  }
  
  public static LogVerifierModule create(Class<?>... targetClasses) {
    return new LogVerifierModule(true, targetClasses);
  }
  
  public static LogVerifierModule create(boolean suppressLogOutput, Class<?>... targetClasses) {
    return new LogVerifierModule(suppressLogOutput, targetClasses);
  }
  
  @Override
  protected void configure() {
    MapBinder<? super Class<?>, LogVerifier> logVerifierBinder =
        MapBinder.newMapBinder(binder(), Class.class, LogVerifier.class);
    for (Class<?> targetClass : targetClasses) {
      logVerifierBinder.addBinding(targetClass)
          .toInstance(new LogVerifier(targetClass, suppressLogOutput));
    }
  }
}
