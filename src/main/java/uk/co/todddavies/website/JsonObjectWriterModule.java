package uk.co.todddavies.website;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.common.annotations.VisibleForTesting;
import com.google.inject.AbstractModule;

@VisibleForTesting
public final class JsonObjectWriterModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(ObjectWriter.class)
        .toInstance(
             new ObjectMapper(new JsonFactory())
                .writer()
                .withDefaultPrettyPrinter());
  }

}
