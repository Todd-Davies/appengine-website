package uk.co.todddavies.website.notes.data;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.appengine.api.datastore.Entity;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;

import java.io.Serializable;
import java.util.List;

@JsonSerialize(using = NotesDocumentSerializer.class)
public final class NotesDocument implements Serializable {
  
  private static final long serialVersionUID = -844261516437759395L;
  
  final String name;
  final String courseCode;
  final String url;
  final ImmutableList<String> tags;
  final long downloads;
  final long key;
  
  //TODO(td): Use autovalue here
  NotesDocument(
      String name, String courseCode, String url, List<String> tags, long downloads, long key) {
    this.name = name;
    this.courseCode = courseCode;
    this.url = url;
    this.tags = ImmutableList.<String>builder().addAll(tags).build();
    this.downloads = downloads;
    this.key = key;
  }
  
  @SuppressWarnings("unchecked")
  static NotesDocument createFromEntity(Entity entity) {
    ImmutableList.Builder<String> tags = ImmutableList.<String>builder();
    for (String s : (List<String>) entity.getProperty("tags")) {
      tags.add(s);
    }
    return new NotesDocument(
        (String) entity.getProperty("name"),
        (String) entity.getProperty("course_code"),
        (String) entity.getProperty("download_url"),
        tags.build(),
        (long) entity.getProperty("downloads"),
        entity.getKey().getId());
  }
  
  @VisibleForTesting
  public static NotesDocument createForTest(
      String name, String courseCode, String url, List<String> tags, int downloads, long key) {
    return new NotesDocument(name, courseCode, url, tags, downloads, key);
  }

  public static Entity incrementDownloads(Entity entity) {
    entity.setProperty("downloads", (long) entity.getProperty("downloads") + 1);
    return entity;
  }
  
  @Override
  public String toString() {
    return new StringBuilder()
        .append("NotesDocument(")
        .append(name).append(",")
        .append(courseCode).append(",")
        .append(url).append(",")
        .append(tags).append(",")
        .append(downloads).append(",")
        .append(key).append(")")
        .toString();
  }
  
  @Override
  public boolean equals(Object other) {
    return other instanceof NotesDocument
        ? this.key == ((NotesDocument) other).key
        : false;
  }

  public String getName() {
    return name;
  }

  public String getUrl() {
    return url;
  }

  public ImmutableList<String> getTags() {
    return tags;
  }

  public long getDownloads() {
    return downloads;
  }

  public long getKey() {
    return key;
  }
  
  public String getCourseCode() {
    return courseCode;
  }
}
