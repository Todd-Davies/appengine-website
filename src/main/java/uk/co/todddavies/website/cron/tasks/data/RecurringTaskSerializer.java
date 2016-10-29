package uk.co.todddavies.website.cron.tasks.data;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

final class RecurringTaskSerializer extends JsonSerializer<RecurringTask> {

  @Override
  public void serialize(RecurringTask value, JsonGenerator gen,
      SerializerProvider serializers) throws IOException,
      JsonProcessingException {
    gen.writeStartObject();
    gen.writeStringField("name", value.name);
    gen.writeStringField("notes", value.notes);
    gen.writeEndArray();
    gen.writeEndObject();
  }
}
