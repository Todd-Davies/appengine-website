$(document).ready(function() {
  $.getJSON('/api/contact', function(data) {
    $('#question').html = "";
    var span = $('<span>').appendTo('#question');
    span.text(data['question']);
    $('<br>').appendTo('#question')
    $('<input>').appendTo('#question')
  });
});
