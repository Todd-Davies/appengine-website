var initPage = function() {
  $.getJSON('/api/notes', function(data) {
    $('#notes-downloads').text("Total downloads: " + data["downloads"]);
    $.each(data["notes"], function(tag, list) {
      $('<h3>').appendTo('#notes-list').text(tag);
      var ul = $('<ul>').appendTo('#notes-list');
      $.each(list, function(j, item) {
        var link = $(document.createElement('a'))
              .attr('href', item["download_url"])
              .text(item["name"] + " - " + item["downloads"]);
          ul.append(
              $(document.createElement('li')).append(link));
      });
    });
    centerPage();
  });
}
