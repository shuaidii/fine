package uk.ac.gla.dcs.bigdata.studentfunctions;

import org.apache.spark.api.java.function.FlatMapFunction;
import uk.ac.gla.dcs.bigdata.providedstructures.NewsArticle;
import uk.ac.gla.dcs.bigdata.studentstructures.ArticleInfo;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class FilterArticleMap implements FlatMapFunction<NewsArticle, ArticleInfo> {
    private static final long serialVersionUID = -3255322884864519662L;

    Set<String> queryTerms;

    public FilterArticleMap(Set<String> queryTerms) {
        this.queryTerms = queryTerms;
    }

    @Override
    public Iterator<ArticleInfo> call(NewsArticle newsArticle) throws Exception {
        Map<String, Integer> queryTermFrequencyMap = new HashMap<>();
        Arrays.stream(newsArticle.getTitle().split(" ")).forEach(word -> {
            if (!queryTerms.contains(word)) {
                return;
            }
            queryTermFrequencyMap.merge(word, 1, Integer::sum);
        });

        AtomicInteger articleLength = new AtomicInteger();
        newsArticle.getContents().forEach(contentItem -> {
            String content = contentItem.getContent();
            String[] words = content.split(",");
            articleLength.addAndGet(words.length);
            for (String word : words) {
                if (!queryTerms.contains(word)) {
                    continue;
                }
                queryTermFrequencyMap.merge(word, 1, Integer::sum);
            }
        });

        List<ArticleInfo> articleInfos = new ArrayList<>();
        if (!queryTermFrequencyMap.isEmpty()) {
            ArticleInfo articleInfo = new ArticleInfo(newsArticle, articleLength.get(), queryTermFrequencyMap);
            articleInfos.add(articleInfo);
        }
        return articleInfos.iterator();
    }

}
