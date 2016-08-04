package uk.co.todddavies.website.notes.data;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

final class NotesDocumentSerializer extends JsonSerializer<NotesDocument> {

  @Override
  public void serialize(NotesDocument value, JsonGenerator gen,
      SerializerProvider serializers) throws IOException,
      JsonProcessingException {
    gen.writeStartObject();
    gen.writeStringField("name", value.name);
    gen.writeStringField("download_url", "/api/notes-dl?key=" + value.key);
    gen.writeNumberField("downloads", value.downloads);
    gen.writeArrayFieldStart("tags");
    for (String tag : value.tags) {
      gen.writeString(tag);
    }
    gen.writeEndArray();
    gen.writeEndObject();
  }
}
