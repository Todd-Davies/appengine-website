package uk.co.todddavies.website.cron.tasks.data;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.appengine.api.datastore.Entity;
import com.google.common.annotations.VisibleForTesting;
import java.io.Serializable;

@JsonSerialize(using = RecurringTaskSerializer.class)
public final class RecurringTask implements Serializable {
  
  private static final long serialVersionUID = -844261516437759395L;
  
  final String name;
  final String notes;
  final long key;
  
  //TODO(td): Use autovalue here
  RecurringTask(
      String name, String notes, long key) {
    this.name = name;
    this.notes = notes;
    this.key = key;
  }
  
  static RecurringTask createFromEntity(Entity entity) {
    return new RecurringTask(
        (String) entity.getProperty("name"),
        (String) entity.getProperty("notes"),
        entity.getKey().getId());
  }
  
  @VisibleForTesting
  public static RecurringTask createForTest(
      String name, String notes, long key) {
    return new RecurringTask(name, notes, key);
  }
  
  @Override
  public String toString() {
    return new StringBuilder()
        .append("RecurringTask(")
        .append(name).append(",")
        .append(notes).append(",")
        .append(key).append(")")
        .toString();
  }
  
  @Override
  public boolean equals(Object other) {
    return other instanceof RecurringTask && this.key == ((RecurringTask) other).key;
  }

  public String getName() {
    return name;
  }

  public String getNotes() {
    return notes;
  }
  public long getKey() {
    return key;
  }
}
