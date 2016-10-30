package uk.co.todddavies.website.credentials;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

final class CredentialSerializer extends JsonSerializer<Credential> {

  @Override
  public void serialize(Credential value, JsonGenerator gen,
      SerializerProvider serializers) throws IOException,
      JsonProcessingException {
    gen.writeStartObject();
    gen.writeStringField("name", value.name);
    gen.writeStringField("value", value.value);
    gen.writeEndArray();
    gen.writeEndObject();
  }
}
