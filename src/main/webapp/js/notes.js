var initPage = function() {
  $.getJSON("/api/notes", function(data) {
    $("#notes-downloads").text("Total downloads: " + data["downloads"]);
    var table = $("<table>").appendTo("#notes-list");
    $.each(data["notes"], function(tag, list) {
      table.append(
          $(document.createElement("tr"))
              .append($(document.createElement("th"))
                  .text(tag)
                  .attr("class", "notes-header-cell")
                  .attr("colspan", "3")));
      table.append(
          $(document.createElement("tr")).attr("class", "notes-header2-cell")
              .append($(document.createElement("th")).text("Name"))
              .append($(document.createElement("th")).text("Course code"))
              .append($(document.createElement("th")).text("Downloads")));
      $.each(list, function(j, item) {
        var tr = $(document.createElement("tr")).attr("class", "notes-body-cell");
        var link = $(document.createElement("a"))
              .attr("href", item["download_url"])
              .text(item["name"]);
        tr.append($(document.createElement("td")).append(link));
        tr.append($(document.createElement("td")).text(item["course_code"]));
        tr.append($(document.createElement("td")).text(item["downloads"]));
        table.append(tr);
      });
    });
  });
};
