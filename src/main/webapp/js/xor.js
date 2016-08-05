/**
 * @author: Todd Davies
 * @date: 2012
 * Encrypts a string using an XOR gate.
 * The ciphertext a sequence of numbers between 0 and 255, seperated by a "-" character.
 * The character "X" is used as padding to make the number 3 characters.
 * The "password" parameter is the password used to en/decrypt the string
 * The "input" paramater is the string to be en/decrypted
 * N.b. Yes, I know this isn"t secure, but it's primary purpose is obfuscation rather than encryption
 */
var xor = function(password, input) {
  if(password.length === 0 || input.length === 0) return "";
  password = password.toLowerCase();
  if(password.length < (input.length / 4)) {
    for (i = 0; password.length < input.length / 4; i++) {
      password += password.charAt(i % password.length);
    }
  }
  var outputString = "";
  var cipherChars = input.split("-");
  for (i = 0; i < password.length; i++) {
    var thisChar = cipherChars[i];
    thisChar = thisChar.replace("X", "");
    outputString += String.fromCharCode(password.charCodeAt(i) ^ parseInt(thisChar, 10));
  }
  return outputString;
}
