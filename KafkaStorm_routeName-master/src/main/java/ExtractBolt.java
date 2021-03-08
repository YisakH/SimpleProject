import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.json.JSONObject;


import java.util.HashMap;
import java.util.Map;

public class ExtractBolt extends BaseRichBolt {
    private OutputCollector collector;
    private HashMap<String, Long> counter = null;

    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.collector = outputCollector;
        this.counter = new HashMap<String, Long>();
    }

    @Override
    public void execute(Tuple tuple) {
        String sentence = tuple.getStringByField("sentence");
        JSONObject json = new JSONObject(sentence);

        String lineName = json.get("line name").toString();
        Long boarding = Long.parseLong(json.get("board").toString());

        this.collector.emit(new Values(lineName, boarding));
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("lineName", "boarding"));
    }
}
