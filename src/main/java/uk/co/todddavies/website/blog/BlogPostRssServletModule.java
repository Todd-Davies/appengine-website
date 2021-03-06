package uk.co.todddavies.website.blog;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;
import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.template.soy.jbcsrc.api.SoySauce;
import uk.co.todddavies.website.cache.MemcacheInterface;
import uk.co.todddavies.website.cache.MemcacheKeys.MemcacheKey;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static uk.co.todddavies.website.blog.BlogPostServletModule.PATH_MAP;

@Singleton
public class BlogPostRssServletModule extends HttpServlet {

  private static final DateTimeFormatter BLOG_DATE_FORMAT = DateTimeFormatter.ofPattern("d.MM.yyyy");
  private static final DateTimeFormatter RSS_DATE_FORMAT = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss ZZZ");
  private static final ZoneId GMT = ZoneId.of("GMT");
  private final SoySauce soySauce;
  private final Provider<MemcacheInterface> memcacheProvider;


  @Inject
  private BlogPostRssServletModule(SoySauce soySauce, Provider<MemcacheInterface> memcacheProvider) {
    this.soySauce = soySauce;
    this.memcacheProvider = memcacheProvider;
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    response.setContentType("application/rss+xml");

    MemcacheInterface memCache = memcacheProvider.get();
    Optional<String> cachedFeed = memCache.get(MemcacheKey.RSS_FEED);

    // The cache expires after 10 minutes, so the feed is never too out of date.
    if (cachedFeed.isPresent()) {
      response.getWriter().print(cachedFeed.get());
    } else {
      String feed = createRssFeed();
      memCache.put(MemcacheKey.RSS_FEED, feed);
      response.getWriter().print(feed);
    }
  }

  private String createRssFeed() throws JsonProcessingException {
    ImmutableList.Builder<Item> posts = ImmutableList.builder();
    for (String path : PATH_MAP.keySet()) {
      String templateName = PATH_MAP.get(path);
      String name = renderText("todddavies.website.blog." + templateName + "Title");
      String description = renderText("todddavies.website.blog." + templateName + "Description");
      String rawDate = renderText("todddavies.website.blog." + templateName + "Date");
      LocalDate date = LocalDate.parse(rawDate, BLOG_DATE_FORMAT);
      String pubDate = date.atStartOfDay(GMT).plusHours(16).format(RSS_DATE_FORMAT);
      String link = "https://todddavies.co.uk/blog/" + path;
      posts.add(new Item(name, link, description, path, pubDate));
    }

    XmlMapper xmlMapper = new XmlMapper();
    return xmlMapper.writeValueAsString(new Rss(posts.build()));
  }

  private String renderText(String templateName) {
    return soySauce.renderTemplate(templateName).renderText().get();
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
    Item(String title, String link, String description, String guid, String pubDate) {
      this.title = title;
      this.link = link;
      this.description = description;
      this.guid = new Guid(guid);
      this.pubDate = pubDate;
    }

    @JacksonXmlProperty private final String title;
    @JacksonXmlProperty private final String link;
    @JacksonXmlProperty private final String description;
    @JacksonXmlProperty private final Guid guid;
    @JacksonXmlProperty private final String pubDate;

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
