package uk.ac.gla.dcs.bigdata.apps;

import java.io.File;
import java.util.*;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import uk.ac.gla.dcs.bigdata.providedfunctions.NewsFormaterMap;
import uk.ac.gla.dcs.bigdata.providedfunctions.QueryFormaterMap;
import uk.ac.gla.dcs.bigdata.providedstructures.DocumentRanking;
import uk.ac.gla.dcs.bigdata.providedstructures.NewsArticle;
import uk.ac.gla.dcs.bigdata.providedstructures.Query;
import uk.ac.gla.dcs.bigdata.providedstructures.RankedResult;
import uk.ac.gla.dcs.bigdata.providedutilities.TextDistanceCalculator;
import uk.ac.gla.dcs.bigdata.studentfunctions.*;
import uk.ac.gla.dcs.bigdata.studentstructures.ArticleInfo;

/**
 * This is the main class where your Spark topology should be specified.
 * <p>
 * By default, running this class will execute the topology defined in the
 * rankDocuments() method in local mode, although this may be overriden by
 * the spark.master environment variable.
 *
 * @author Richard
 */
public class AssessedExercise {


    public static void main(String[] args) {

        File hadoopDIR = new File("resources/hadoop/"); // represent the hadoop directory as a Java file so we can get an absolute path for it
        System.setProperty("hadoop.home.dir", hadoopDIR.getAbsolutePath()); // set the JVM system property so that Spark finds it

        // The code submitted for the assessed exerise may be run in either local or remote modes
        // Configuration of this will be performed based on an environment variable
        String sparkMasterDef = System.getenv("spark.master");
        if (sparkMasterDef == null) sparkMasterDef = "local[2]"; // default is local mode with two executors

        String sparkSessionName = "BigDataAE"; // give the session a name

        // Create the Spark Configuration
        SparkConf conf = new SparkConf()
                .setMaster(sparkMasterDef)
                .setAppName(sparkSessionName);

        // Create the spark session
        SparkSession spark = SparkSession
                .builder()
                .config(conf)
                .getOrCreate();


        // Get the location of the input queries
        String queryFile = System.getenv("bigdata.queries");
        if (queryFile == null) queryFile = "data/queries.list"; // default is a sample with 3 queries

        // Get the location of the input news articles
        String newsFile = System.getenv("bigdata.news");
        if (newsFile == null)
            newsFile = "data/TREC_Washington_Post_collection.v3.example.json"; // default is a sample of 5000 news articles

        // Call the student's code
        List<DocumentRanking> results = rankDocuments(spark, queryFile, newsFile);

        // Close the spark session
        spark.close();

        // Check if the code returned any results
        if (results == null)
            System.err.println("Topology return no rankings, student code may not be implemented, skiping final write.");
        else {

            // We have set of output rankings, lets write to disk

            // Create a new folder
            File outDirectory = new File("results/" + System.currentTimeMillis());
            if (!outDirectory.exists()) outDirectory.mkdir();

            // Write the ranking for each query as a new file
            for (DocumentRanking rankingForQuery : results) {
                rankingForQuery.write(outDirectory.getAbsolutePath());
            }
        }


    }


    public static List<DocumentRanking> rankDocuments(SparkSession spark, String queryFile, String newsFile) {

        // Load queries and news articles
        Dataset<Row> queriesjson = spark.read().text(queryFile);
        Dataset<Row> newsjson = spark.read().text(newsFile); // read in files as string rows, one row per article

        // Perform an initial conversion from Dataset<Row> to Query and NewsArticle Java objects
        Dataset<Query> queries = queriesjson.map(new QueryFormaterMap(), Encoders.bean(Query.class)); // this converts each row into a Query
        Dataset<NewsArticle> news = newsjson.map(new NewsFormaterMap(), Encoders.bean(NewsArticle.class)); // this converts each row into a NewsArticle

        //----------------------------------------------------------------
        // Your Spark Topology should be defined here
        //----------------------------------------------------------------
        //Prepare for DPHScore
        long articleSize = news.count();

        Dataset<Integer> articleLengthDataset = news.map(new ArticleLengthMap(), Encoders.INT());
        Integer articleLength = articleLengthDataset.reduce(new ArticleLengthReducer());
        double averageArticleLength = articleLength * 1.0D / articleSize;

        Set<String> queryTerms = new HashSet<String>();
        List<Query> queryList = queries.collectAsList();
        for (Query query : queryList) {
            queryTerms.addAll(query.getQueryTerms());
        }

        Dataset<ArticleInfo> articleInfoDataset = news.flatMap(new FilterArticleMap(queryTerms), Encoders.bean(ArticleInfo.class));
        ArticleInfo articleInfo = articleInfoDataset.reduce(new PairReducer());
        Broadcast<ArticleInfo> articleInfoBroadcast = JavaSparkContext.fromSparkContext(spark.sparkContext()).broadcast(articleInfo);

        List<DocumentRanking> rankList = new ArrayList<>();
        for (Query query : queryList) {
            Set<String> queryTermsTmp = new HashSet<>(query.getQueryTerms());
            Dataset<ArticleInfo> articleInfoDatasetTmp = news.flatMap(new FilterArticleMap(queryTermsTmp), Encoders.bean(ArticleInfo.class));
            QueryFlatMap queryFlatMap = new QueryFlatMap(queryTerms, articleInfoBroadcast, averageArticleLength, articleSize);

            Dataset<RankedResult> rankedResultDataset = articleInfoDatasetTmp.flatMap(queryFlatMap, Encoders.bean(RankedResult.class));
            List<RankedResult> top10List = getTop10RankedResult(rankedResultDataset.collectAsList());
            rankList.add(new DocumentRanking(query, top10List));
        }

        return rankList;
    }

    private static List<RankedResult> getTop10RankedResult(List<RankedResult> rankResultList) {
        Collections.sort(rankResultList);
        Collections.reverse(rankResultList);

        List<RankedResult> resultList = new ArrayList<>();
        for (RankedResult rankedResult : rankResultList) {
            if (resultList.size() >= 10) {
                break;
            }

            String title = rankedResult.getArticle().getTitle();
            boolean contains = resultList.stream().anyMatch(result ->
                    TextDistanceCalculator.similarity(title, result.getArticle().getTitle()) < 0.5);
            if (!contains) {
                resultList.add(rankedResult);
            }
        }

        return resultList;
    }


}
