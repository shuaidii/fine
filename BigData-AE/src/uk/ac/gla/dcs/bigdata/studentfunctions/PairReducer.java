package uk.ac.gla.dcs.bigdata.studentfunctions;

import org.apache.spark.api.java.function.ReduceFunction;
import uk.ac.gla.dcs.bigdata.studentstructures.ArticleInfo;
import java.util.HashMap;
import java.util.Map;

public class PairReducer implements ReduceFunction<ArticleInfo>{
    private static final long serialVersionUID = 8815987343574012980L;

	@Override
	public ArticleInfo call(ArticleInfo v1, ArticleInfo v2) throws Exception {
		Map<String, Integer> newFrequencyMap = new HashMap<>(v1.getQueryTermFrequencyMap());
        for (Map.Entry<String, Integer> entry : v2.getQueryTermFrequencyMap().entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();
            newFrequencyMap.merge(key, value, Integer::sum);
        }
        return new ArticleInfo(newFrequencyMap);
	}

}
