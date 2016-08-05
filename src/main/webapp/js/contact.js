var answer = "";

var initPage = function() {
  $.getJSON("/api/contact", function(data) {
    $("#contact-question").html("");
    var span = $("<span>").appendTo("#contact-question");
    span.text(data["question"]);
    answer = data["answer"];
    $("<br>").appendTo("#contact-question");
    var wrapper = $("<div>").attr("id", "contact-wrapper").appendTo("#contact-question");
    $("<input>")
        .attr("onkeyup", "submitAnswer();")
        .appendTo("#contact-wrapper");
    $("<input>")
        .attr("class", "refreshButton")
        .attr("label", "refreshQuestion")
        .attr("type", "button")
        .attr("onclick", "refreshQuestion();")
        .appendTo("#contact-wrapper");
    $("<p>").appendTo("#contact-question");
  });
};

var refreshQuestion = function() {
  $(".refreshQuestion").disabled = true;
  initPage();
}

var submitAnswer = function() {
  var text = $("#contact-question input").val();
  $("#contact-question p").text(xor(text, answer));
}