package rssminer.sax;

import java.net.URI;
import java.util.regex.Pattern;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

//952 http://www.uc.hk/?feed=rss2
//2108 http://developers.sun.com/rss/mobility.xml
//id:114 200 http://planet.clojure.in/ [0] feeds

public class ExtractRssHandler extends DefaultHandler {
    private final URI base;

    public ExtractRssHandler(URI base) {
        this.base = base;
    }

    final static String HREF = "href";
    final static String LINK = "link";
    final static String RSS = "application/rss+xml";
    final static String ATOM = "application/atom+xml";
    final static String TITLE = "title";
    final static String TYPE = "type";

    private String rssLink;

    final static Pattern comment = Pattern.compile("comment",
            Pattern.CASE_INSENSITIVE);

    public void startElement(String uri, String localName, String qName,
            Attributes attrs) throws SAXException {
        if (LINK.equalsIgnoreCase(qName)) {
            int index = attrs.getIndex(TYPE);
            if (index != -1) {
                String type = attrs.getValue(index);
                if (RSS.equalsIgnoreCase(type) || ATOM.equalsIgnoreCase(type)) {
                    String href = attrs.getValue(HREF);
                    String title = attrs.getValue(TITLE);
                    // ignore comment
                    if (!comment.matcher(href).find()
                            && !comment.matcher(title).find()) {
                        // TODO last one
                        rssLink = base.resolve(href).toString();
                    }
                }
            }
        }
    }

    public String getRss() {
        return rssLink;
    }
}