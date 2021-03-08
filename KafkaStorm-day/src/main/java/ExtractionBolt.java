import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.json.JSONObject;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ExtractionBolt extends BaseRichBolt {
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
        String date = new String(json.get("date").toString());

        // 아예 date format을 처음부터 바꿈
        try {
            date= dateformat_fn(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String alight_str = new String(json.get("board").toString());
        Long alight = Long.parseLong(alight_str);

        collector.emit(new Values(date, alight));
    }

    public String dateformat_fn(String date) throws ParseException {

        Date dateparse = new SimpleDateFormat("yyyyMMdd").parse(date);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월 dd일");
        String new_date = sdf.format(dateparse);
        return new_date;


    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("date", "alight"));
    }

}
