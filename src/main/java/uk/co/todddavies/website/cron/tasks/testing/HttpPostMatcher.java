package uk.co.todddavies.website.cron.tasks.testing;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPost;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Hacky matcher for comparing HttpPost objects
 * 
 * Note that the ordering of the url parameters is implicitly tested for.
 */
public final class HttpPostMatcher extends BaseMatcher<HttpPost> {

  private final HttpPost expected;
  
  private HttpPostMatcher(HttpPost expected) {
    this.expected = expected;
  }
  
  public static HttpPostMatcher match(HttpPost expected) {
    return new HttpPostMatcher(expected);
  }
  
  @Override
  public boolean matches(Object in) {
    if (!(in instanceof HttpPost)) {
      return false;
    }
    HttpPost given = (HttpPost) in;
    return compareHeaders(given.getAllHeaders()) && compareEntity(given.getEntity());
  }
  
  private boolean compareHeaders(Header[] givenHeaders) {
    for (Header givenHeader : givenHeaders) {
      if (!expected.containsHeader(givenHeader.getName()) || 
          !expected.getFirstHeader(givenHeader.getName()).getValue()
              .equals(givenHeader.getValue())) {
          return false;
      }
    }
    return true;
  }
  
  private boolean compareEntity(HttpEntity givenEntity) {
    return getContent(givenEntity).equals(getContent(expected.getEntity()));
  }
  
  private String getContent(HttpEntity entity) {
    try {
      BufferedReader isr = new BufferedReader(new InputStreamReader(entity.getContent()));
      String line = isr.readLine();
      StringBuilder out = new StringBuilder();
      while (line != null) {
        out.append(line).append("\n");
        line = isr.readLine();
      }
      return out.toString();
    } catch (IllegalStateException | IOException e) {
      e.printStackTrace();
      return "";
    }
  }

  @Override
  public void describeTo(Description description) {
    // Required
  }
}
