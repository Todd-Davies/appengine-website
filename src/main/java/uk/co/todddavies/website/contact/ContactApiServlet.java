package uk.co.todddavies.website.contact;

import uk.co.todddavies.website.contact.Annotations.EmailAddress;
import uk.co.todddavies.website.contact.captcha.CaptchaQuestion;

import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Singleton
@SuppressWarnings("serial")
final class ContactApiServlet extends HttpServlet {
  
  @VisibleForTesting
  static final String SESSION_CONTACT_PRESSES = "contact-presses";
  private static final Logger log = Logger.getLogger(ContactApiServlet.class.getName());
  
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
    
    CaptchaQuestion question = getQuestion(req.getSession());
    
    resp.getWriter().print(
        jsonObjectWriter.writeValueAsString(
            ImmutableMap.of(
                "question", question.getQuestion(),
                "answer", question.encryptSecret(email))));
  }
  
  private CaptchaQuestion getQuestion(HttpSession session) {
    Object numPresses = session.getAttribute(SESSION_CONTACT_PRESSES);
    // TODO(td): Don't hard code '5'
    // TODO(td): Check the type of the object before casting
    if (numPresses == null || (int) numPresses < 5) {
      session.setAttribute(
          SESSION_CONTACT_PRESSES,
          numPresses == null ? 1 : (int) numPresses + 1);
      return questionProvider.get();
    } else {
      log.log(Level.INFO, "User pressed the contact button 5 times.");
      session.setAttribute(SESSION_CONTACT_PRESSES, 0);
      return questionProvider.get();
    }
  }
}
