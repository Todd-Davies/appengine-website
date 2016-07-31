package uk.co.todddavies.website.notes;

import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.StringValue;
import com.google.cloud.datastore.Value;
import com.google.common.collect.ImmutableList;

@JsonSerialize(using = NotesDocumentSerializer.class)
final class NotesDocument {
  final String name, url;
  final ImmutableList<String> tags;
  final int downloads;
  
  //TODO(td): Use autovalue here
  NotesDocument(String name, String url, List<String> tags, int downloads) {
    this.name = name;
    this.url = url;
    this.tags = ImmutableList.<String>builder().addAll(tags).build();
    this.downloads = downloads;
  }
  
  static NotesDocument createFromEntity(Entity entity) {
    ImmutableList.Builder<String> tags = ImmutableList.<String>builder();
    for (Value<String> s : entity.<StringValue>getList("tags")) {
      tags.add(s.get());
    }
    return new NotesDocument(
        entity.getString("name"),
        entity.getString("download_url"),
        tags.build(),
        (int) entity.getLong("downloads"));
  }
}
