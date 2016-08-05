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
    var span = $("<p>").appendTo("#contact-question");
  });
}

var refreshQuestion = function() {
  $(".refreshQuestion").disabled = true;
  initPage();
}

var submitAnswer = function() {
  var text = $("#contact-question input").val();
  $("#contact-question p").text(xor(false, text, answer));
}

/**
 * @author: Todd Davies
 * @date: 2012
 * Encrypts a string using an XOR gate.
 * The ciphertext a sequence of numbers between 0 and 255, seperated by a "-" character. The character "X" is used as padding to make the number 3 characters.
 * The "mode" parameter is true for encryption, false for decryption
 * The "password" parameter is the password used to en/decrypt the string
 * The "input" paramater is the string to be en/decrypted
 * N.b. Yes, I know this isn"t secure, but it's primary purpose is obfuscation rather than encryption
 */
var xor = function(mode, password, input) {
  if(password.length==0||input.length==0) return ""; //If there was no input
  password = password.toLowerCase();
  if(password.length<(input.length/4)) {
    for (var i = 0; password.length < input.length / 4; i++) {
      password += password.charAt(i % password.length);
    }
  }
  var outputString = "";
  var cipherChars = input.split("-");
  for (var i=0;i<password.length;i++) {
    var thisChar = cipherChars[i];
    thisChar = thisChar.replace("X", "");
    outputString += String.fromCharCode(password.charCodeAt(i) ^ parseInt(thisChar, 10));
  }
  return outputString;
}