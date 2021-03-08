import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.json.JSONObject;


import java.lang.invoke.SwitchPoint;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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

        Long board = Long.parseLong(json.get("board").toString());

        // 아예 date format을 처음부터 바꿈
        String week = null;
        try {
            week= week_fn(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //System.out.println("ExtractionBolt) 날짜: "+date+", 출입: "+board);
        collector.emit(new Values(week, board));
        //System.out.println(sentence);
    }

    public String week_fn(String date) throws ParseException {

        Date dateparse = new SimpleDateFormat("yyyyMMdd").parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateparse);
        int day = cal.get(Calendar.DAY_OF_WEEK);
        String to = Integer.toString(day);

        return to;
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("week", "board"));
    }
}
