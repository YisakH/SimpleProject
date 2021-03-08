import clojure.lang.IFn;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;

import java.text.DecimalFormat;
import java.util.*;

public class ReportBolt extends BaseRichBolt {

    private HashMap<String, Long> counter = null;
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
        this.counter = new HashMap<String, Long>();
        printMiddle = new PrintMiddle();
    }

    @Override
    public void execute(Tuple tuple) {
        String week = tuple.getStringByField("week2");
        Long sum = tuple.getLongByField("sum");
        this.counter.put(week, sum);

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
        String sum;
        System.out.println("------- " + name + " -------");

        for (String key : keys) {
            sum = decimalFormat.format(this.counter.get(key));
            switch (key) {
                case "1":
                    System.out.println("일요일" + ": " + sum);
                    break;
                case "2":
                    System.out.println("월요일" + ": " + sum);
                    break;
                case "3":
                    System.out.println("화요일" + ": " + sum);
                    break;
                case "4":
                    System.out.println("수요일" + ": " + sum);
                    break;
                case "5":
                    System.out.println("목요일" + ": " + sum);
                    break;
                case "6":
                    System.out.println("금요일" + ": " + sum);
                    break;
                case "7":
                    System.out.println("토요일" + ": " + sum);
                    break;
            }
        }
        System.out.println("---------------------------");
    }
}
