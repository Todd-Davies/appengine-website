package uk.co.todddavies.website.credentials;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.appengine.api.datastore.Entity;
import com.google.common.annotations.VisibleForTesting;
import java.io.Serializable;

@JsonSerialize(using = CredentialSerializer.class)
public final class Credential implements Serializable {
  
  private static final long serialVersionUID = -844261516437759395L;
  
  final String name;
  final String value;
  final long key;
  
  //TODO(td): Use autovalue here
  Credential(String name, String value, long key) {
    this.name = name;
    this.value = value;
    this.key = key;
  }
  
  static Credential createFromEntity(Entity entity) {
    return new Credential(
        (String) entity.getProperty("name"),
        (String) entity.getProperty("value"),
        entity.getKey().getId());
  }
  
  @VisibleForTesting
  public static Credential createForTest(
      String name, String value, long key) {
    return new Credential(name, value, key);
  }
  
  @Override
  public String toString() {
    return new StringBuilder()
        .append("NotesDocument(")
        .append(name).append(",")
        .append(value).append(",")
        .append(key).append(")")
        .toString();
  }
  
  @Override
  public boolean equals(Object other) {
    return other instanceof Credential
        ? this.key == ((Credential) other).key
        : false;
  }

  public String getName() {
    return name;
  }

  public String getValue() {
    return value;
  }
  public long getKey() {
    return key;
  }
}
