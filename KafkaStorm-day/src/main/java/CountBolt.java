import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.util.HashMap;
import java.util.Map;

public class CountBolt extends BaseRichBolt {
    private OutputCollector collector;
    private HashMap<String, Long> counter = null;

    @Override
    public void prepare(Map map, TopologyContext topologyContext,
                        OutputCollector outputCollector) {
        this.collector = outputCollector;
        this.counter = new HashMap<String, Long>();
    }

    @Override
    public void execute(Tuple tuple) {
        String date = tuple.getStringByField("date");

        Long alight = tuple.getLongByField("alight");

        Long sum = this.counter.get(date);
        sum = (sum == null) ? alight :sum+alight;

        this.counter.put(date, sum);
        this.collector.emit(new Values(date, sum));
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        // date2 로 보냄
        outputFieldsDeclarer.declare(new Fields("date2", "sum"));
    }
}
