package uk.co.todddavies.website.contact;

import java.io.IOException;
import java.util.LinkedHashMap;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import uk.co.todddavies.website.contact.Annotations.EmailAddress;
import uk.co.todddavies.website.contact.captcha.CaptchaQuestion;

import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

@Singleton
@SuppressWarnings("serial")
final class ContactApiServlet extends HttpServlet {
  
  private final String email;
  private final Provider<CaptchaQuestion> questionProvider;
  private final ObjectWriter jsonObjectWriter;
  
  @Inject
  private ContactApiServlet(@EmailAddress String email,
      ObjectWriter jsonObjectWriter,
      Provider<CaptchaQuestion> questionProvider) {
    this.email = email;
    this.questionProvider = questionProvider;
    this.jsonObjectWriter = jsonObjectWriter;
  }
  
  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    resp.setContentType("text/plain");
    CaptchaQuestion randomQuestion = questionProvider.get();
    LinkedHashMap<String, String> response = new LinkedHashMap<>();
    response.put("question", randomQuestion.getQuestion());
    response.put("answer", randomQuestion.encryptSecret(email));
    resp.getWriter().print(jsonObjectWriter.writeValueAsString(response));
  }
}
