var currentPage = "";
var initPage = function() {}

$(document).ready(function() {
  $.getJSON("/api/pages", function(data) {
    // Load the home page first
    loadPage(data["home"]);
    // Now render the page list 
    $("#pages").html("");
    var ul = $("#pages");
    var pages = data["pages"];
    for (var key in pages) {
      if (pages.hasOwnProperty(key)) {
        var link = $(document.createElement("a"))
            .attr("href", "#")
            .attr("onclick", "loadPage(\"" + key + "\");")
            .text(pages[key]);
        ul.append(
            $(document.createElement("li")).append(link)
        );
      }
    }
  });
});

var loadPage = function(url) {
  if (currentPage == url) return;
  
  currentPage = url;
  
  $("#content").load(url, function( response, status, xhr ) {
    if (status !== "error") {
      initPage();
    } else {
      console.log("There was an error loading " + url);
    }
  });
};