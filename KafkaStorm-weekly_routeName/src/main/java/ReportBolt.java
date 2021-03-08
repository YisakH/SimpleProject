import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;

import java.text.DecimalFormat;
import java.util.*;

public class ReportBolt extends BaseRichBolt {

    private HashMap<String, ArrayList<Long>> counter = null;
    private PrintMiddle printMiddle = null;
    boolean threadStop = false;

    DecimalFormat decimalFormat = new DecimalFormat("###,###,###");

    class PrintMiddle implements Runnable{
        @Override
        public void run() {
            try {
                while(counter.isEmpty())
                    Thread.sleep(10000);

                List<String> keyList = new ArrayList<String>(counter.keySet());

                while (!threadStop) {
                    Thread.sleep(60000);

                    if(keyList.size() != counter.keySet().size()){
                        keyList = new ArrayList<String>(counter.keySet());
                        Collections.sort(keyList);
                    }

                    if (counter.size() > 0) {
                        print(keyList, "MIDDLE COUNT");
                    }
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        public PrintMiddle(){
            new Thread(this).start();
        }
    }

    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.counter = new HashMap<>();
        printMiddle = new PrintMiddle();
    }

    @Override
    public void execute(Tuple tuple) {
        int week = tuple.getIntegerByField("weekday");
        String lineName = tuple.getStringByField("lineName");
        Long sum = tuple.getLongByField("sum");

        ArrayList<Long> list = this.counter.get(lineName);
        if(list==null) {
            list = new ArrayList<>();
            for(int i=0; i<= 7; i++)
                list.add(null);
        }

        list.set(week, sum);

        this.counter.put(lineName, list);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
    }

    @Override
    public void cleanup() {
        threadStop = true;
        // 정렬
        List<String> keys = new ArrayList<>(this.counter.keySet());
        Collections.sort(keys);

        print(keys, "FINAL COUNT");
    }


    private void print(List<String> keys, String name) {

        System.out.println("------------------------- " + name + " ---------------------------");
        System.out.println("-----  일요일   월요일   화요일   수요일   목요일   금요일   토요일  -----");

        for (String key : keys) {
            List<Long> list = this.counter.get(key);
            String output = String.format("%10s", key);
            for(int day = 1; day <= 7; day++) {
                output += String.format("%9d", list.get(day));
            }
            System.out.println(output);
        }
        System.out.println("------------------------------------------------------------------");
    }
}
