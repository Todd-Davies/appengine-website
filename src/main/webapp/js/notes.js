var initPage = function() {
  $.getJSON("/api/notes", function(data) {
    $("#notes-downloads").text("Total downloads: " + data["downloads"]);
    $.each(data["notes"], function(tag, list) {
      $("<h3>").appendTo("#notes-list").text(tag);
      var table = $("<table>").appendTo("#notes-list");
      table.append(
          $(document.createElement("tr"))
              .append($(document.createElement("th")).text("Name"))
              .append($(document.createElement("th")).text("Downloads")));
      $.each(list, function(j, item) {
        var tr = $(document.createElement("tr"));
        var link = $(document.createElement("a"))
              .attr("href", item["download_url"])
              .text(item["name"]);
        tr.append($(document.createElement("td")).append(link));
        tr.append($(document.createElement("td")).text(item["downloads"]));
        table.append(tr);
      });
    });
  });
};
