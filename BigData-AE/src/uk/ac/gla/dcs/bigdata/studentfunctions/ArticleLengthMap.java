package uk.ac.gla.dcs.bigdata.studentfunctions;

import org.apache.commons.lang3.StringUtils;
import org.apache.spark.api.java.function.MapFunction;
import uk.ac.gla.dcs.bigdata.providedstructures.ContentItem;
import uk.ac.gla.dcs.bigdata.providedstructures.NewsArticle;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ArticleLengthMap implements MapFunction<NewsArticle, Integer> {
    private static final long serialVersionUID = 1011630381790918245L;

    @Override
    public Integer call(NewsArticle value) throws Exception {
        AtomicInteger articleLength = new AtomicInteger();
        value.getContents().stream()
                .filter(contentItem -> contentItem != null && StringUtils.isNotBlank(l.getContent()))
                .forEach(contentItem -> {
                    String content = contentItem.getContent();
                    articleLength.addAndGet(content.split(",").length);
                });
        return articleLength.get();
    }

}
