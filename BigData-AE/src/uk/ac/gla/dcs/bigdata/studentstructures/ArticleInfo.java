package uk.ac.gla.dcs.bigdata.studentstructures;

import uk.ac.gla.dcs.bigdata.providedstructures.NewsArticle;

import java.io.Serializable;
import java.util.Map;

public class ArticleInfo implements Serializable {
    NewsArticle newsArticle;
    int articleLength;
    Map<String, Integer> queryTermFrequencyMap;

    public ArticleInfo(NewsArticle newsArticle) {
        this.newsArticle = newsArticle;
    }

    public ArticleInfo(Map<String, Integer> queryTermFrequencyMap) {
        this.queryTermFrequencyMap = queryTermFrequencyMap;
    }

    public ArticleInfo(NewsArticle newsArticle, int articleLength, Map<String, Integer> queryTermFrequencyMap) {
        this(queryTermFrequencyMap);
        this.newsArticle = newsArticle;
    }

    public Map<String, Integer> getQueryTermFrequencyMap() {
        return queryTermFrequencyMap;
    }

    public NewsArticle getNewsArticle() {
        return newsArticle;
    }

    public int getArticleLength() {
        return articleLength;
    }

}
