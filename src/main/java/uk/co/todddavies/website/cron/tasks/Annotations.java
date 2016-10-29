package uk.co.todddavies.website.cron.tasks;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import com.google.inject.BindingAnnotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

final class Annotations {

  private Annotations() {}
  
  /* Package private binding annotation for the actual memcache object (as opposed to the 
   * MemcacheInterface wrappers) */
  @BindingAnnotation @Target({ FIELD, PARAMETER, METHOD }) @Retention(RUNTIME)
  @interface TaskId {}
}
