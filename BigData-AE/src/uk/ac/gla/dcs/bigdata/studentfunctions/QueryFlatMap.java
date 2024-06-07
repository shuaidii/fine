package uk.ac.gla.dcs.bigdata.studentfunctions;

import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.broadcast.Broadcast;
import uk.ac.gla.dcs.bigdata.providedstructures.RankedResult;
import uk.ac.gla.dcs.bigdata.providedutilities.DPHScorer;
import uk.ac.gla.dcs.bigdata.studentstructures.ArticleInfo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class QueryFlatMap implements FlatMapFunction<ArticleInfo, RankedResult> {
    private static final long serialVersionUID = 7261200823734376721L;

    Set<String> queryTerms;
    Broadcast<ArticleInfo> articleInfoBroadcast;
    double averageArticleLength;
    long articleSize;

    public QueryFlatMap(Set<String> queryTerms, Broadcast<ArticleInfo> articleInfoBroadcast,
                        double averageArticleLength, long articleSize) {
        this.queryTerms = queryTerms;
        this.articleInfoBroadcast = articleInfoBroadcast;
        this.averageArticleLength = averageArticleLength;
        this.articleSize = articleSize;
    }

    @Override
    public Iterator<RankedResult> call(ArticleInfo articleInfo) throws Exception {
        short queryTermFrequencyTmp = 0;
        int broadcastTermFrequencyInCorpus = 0;
        double scoreSum = 0.0;
        ArticleInfo broadcastValue = articleInfoBroadcast.getValue();
        for (String queryTerm : queryTerms) {
            if (articleInfo.getQueryTermFrequencyMap().containsKey(queryTerm)) {
                queryTermFrequencyTmp += articleInfo.getQueryTermFrequencyMap().get(queryTerm);
            }
            if (broadcastValue.getQueryTermFrequencyMap().containsKey(queryTerm)) {
                broadcastTermFrequencyInCorpus += broadcastValue.getQueryTermFrequencyMap().get(queryTerm);
            }

            if (articleInfo.getArticleLength()>0) {
                double dphScore = DPHScorer.getDPHScore(queryTermFrequencyTmp, broadcastTermFrequencyInCorpus,
                        articleInfo.getArticleLength(), averageArticleLength, articleSize);
                if (!Double.isNaN(dphScore)) {
                    scoreSum += dphScore;
                }
            }
        }

        double  score = scoreSum / queryTerms.size();
        List<RankedResult> rankedResults = new ArrayList<>();
        if (!(score < Double.MIN_VALUE)) {
            rankedResults.add(new RankedResult(articleInfo.getNewsArticle().getId(), articleInfo.getNewsArticle(), score));
        }
        return rankedResults.iterator();
    }
}
