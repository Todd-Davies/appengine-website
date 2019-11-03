package uk.co.todddavies.website.blog;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;
import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static uk.co.todddavies.website.blog.BlogPostServletModule.PATH_MAP;

@Singleton
public class BlogPostRssServletModule extends HttpServlet {

  @Inject
  private BlogPostRssServletModule() {}

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    response.setContentType("application/rss+xml");

    ImmutableList.Builder<Item> posts = ImmutableList.builder();
    for (String path : PATH_MAP.values()) {
      // TODO: Set a proper name
      // TODO: Set a proper description
      String link = "https://todddavies.co.uk/blog/" + path;
      posts.add(new Item(link, link, "", path));
    }

    XmlMapper xmlMapper = new XmlMapper();
    response.getWriter().print(xmlMapper.writeValueAsString(new Rss(posts.build())));
  }

  @JacksonXmlRootElement(localName = "rss")
  class Rss {
    Rss(List<Item> items) {
      this.channel = new Channel(items);
    }

    @JacksonXmlProperty(isAttribute = true)
    private final double version = 2.0;

    @JacksonXmlProperty(isAttribute = true, localName = "xmlns:dc")
    private final String dc = "http://purl.org/dc/elements/1.1/";

    @JacksonXmlProperty(isAttribute = true, localName = "xmlns:media")
    private final String media = "http://search.yahoo.com/mrss/";

    @JacksonXmlProperty(isAttribute = true, localName = "xmlns:atom")
    private final String atom = "http://www.w3.org/2005/Atom";

    @JacksonXmlProperty
    private final Channel channel;

    @JacksonXmlRootElement(localName = "channel")
    @JsonPropertyOrder({ "title", "link", "description", "language", "ttl", "item" })
    private class Channel {
      Channel(List<Item> items) {
        this.item = items;
      }

      @JacksonXmlProperty private final String title = "Todd Davies' Blog";
      @JacksonXmlProperty private final String link = "https://todddavies.co.uk/blog";
      @JacksonXmlProperty private final String description = "Todd Davies' blog.";
      @JacksonXmlProperty private final String language = "en";
      @JacksonXmlProperty private final int ttl = 180;

      @JacksonXmlProperty
      @JacksonXmlElementWrapper(useWrapping = false)
      private final List<Item> item;
    }
  }

  @JacksonXmlRootElement(localName = "item")
  class Item {
    Item(String title, String link, String description, String guid) {
      this.title = title;
      this.link = link;
      this.description = description;
      this.guid = new Guid(guid);
    }

    @JacksonXmlProperty private final String title;
    @JacksonXmlProperty private final String link;
    @JacksonXmlProperty private final String description;
    @JacksonXmlProperty private final Guid guid;

    @JacksonXmlRootElement(localName = "guid")
    private class Guid {
      Guid(String guid) {
        this.guid = guid;
      }

      @JacksonXmlProperty(isAttribute = true)
      private final boolean isPermaLink = false;

      @JacksonXmlText private final String guid;
    }
  }
}
