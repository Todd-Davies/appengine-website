$(document).ready(function() {
  $.getJSON('/api/pages', function(data) {
    $('#content').html = "";
    var ul = $('<ul>').appendTo('#pages');
    for (var key in data) {
      if (data.hasOwnProperty(key)) {
        var link = $(document.createElement('a')).attr("href", key).text(data[key]);
        ul.append(
            $(document.createElement('li')).append(link)
        );
      }
    }
  });
});
