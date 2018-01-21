var currentPage = "";
var initPage = function() {};

var loadPage = function(url) {
  if (currentPage === url) {
    return;
  }
  
  currentPage = url;
  
  $("#content").load(url, function( response, status, xhr ) {
    if (status !== "error") {
      initPage();
    } else {
      console.log("There was an error loading " + url);
    }
  });
};

$(document).ready(function() {
  $.getJSON("/api/pages", function(data) {
    // Now render the page list 
    $("#pages").html("");
    var ul = $("#pages");
    var pages = data["pages"];
    for (var key in pages) {
      if (pages.hasOwnProperty(key)) {
        var link = $(document.createElement("a"))
            .attr("href", "#" + pages[key])
            .attr("onclick", "loadPage(\"" + key + "\");")
            .text(pages[key]);
        ul.append(
            $(document.createElement("li")).append(link)
        );
        var currentHeader = window.location.hash.substr(1);
        if (pages[key].toLowerCase() === currentHeader.toLowerCase()) {
          loadPage(key);
        }
      }
    }
    if (window.location.hash.substr(1) === "") {
      loadPage(data["home"]);
    }
  });
});
