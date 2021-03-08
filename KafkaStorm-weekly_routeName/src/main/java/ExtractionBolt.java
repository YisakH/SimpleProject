import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.json.JSONObject;
import scala.Int;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ExtractionBolt extends BaseRichBolt {
    private OutputCollector collector;
    private Map<Integer, Long> map = null;

    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.collector = outputCollector;
    }

    @Override
    public void execute(Tuple tuple) {
        String sentence = tuple.getStringByField("sentence");
        JSONObject json = new JSONObject(sentence);
        String date = json.get("date").toString();

        String lineName = json.get("line name").toString();
        Long board = Long.parseLong(json.get("board").toString());


        HashMap<Integer, Long> counter = new HashMap<>();


        // 아예 date format을 처음부터 바꿈
        int weekday = 0;
        try {
            weekday= week_fn(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        counter.put(weekday, board);
        collector.emit(new Values(lineName, weekday, board));
    }

    public int week_fn(String date) throws ParseException {
        Date dateparse = new SimpleDateFormat("yyyyMMdd").parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateparse);
        int day = cal.get(Calendar.DAY_OF_WEEK);

        return day;
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("lineName", "weekday", "board"));
    }
}
