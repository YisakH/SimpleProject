import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.lang.reflect.Array;
import java.util.*;

public class CountBolt extends BaseRichBolt {
    private OutputCollector collector;
    private HashMap<String, ArrayList<Long>> counter = null;

    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.collector = outputCollector;
        this.counter = new HashMap<String, ArrayList<Long>>();
    }

    @Override
    public void execute(Tuple tuple) {

        String lineName = tuple.getStringByField("lineName");
        Integer weekday = tuple.getIntegerByField("weekday");
        Long board = tuple.getLongByField("board");

        System.out.println(lineName + ", " + weekday + ", " + board);

        ArrayList<Long> list = counter.get(lineName);
        if(list==null) {
            list = new ArrayList<>();
            for(int i=0; i<= 7; i++)
                list.add(null);
        }

        Long sum = list.get(weekday);
        sum = (sum==null) ? board : board + sum;

        list.set(weekday, sum);
        this.counter.put(lineName, list);
        this.collector.emit(new Values(lineName, weekday, sum));
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        // date2 로 보냄
        outputFieldsDeclarer.declare(new Fields("lineName", "weekday", "sum"));
    }
}
