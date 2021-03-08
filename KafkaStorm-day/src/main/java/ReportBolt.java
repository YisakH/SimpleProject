import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;

import java.util.*;

public class ReportBolt extends BaseRichBolt {
    private HashMap<String, Long> counter = null;
    private PrintMiddle printMiddle = null;

    class PrintMiddle implements Runnable{
        boolean runBit = true;

        @Override
        public void run() {
            try {
                while(counter.isEmpty())
                    Thread.sleep(10000);

                System.out.println("Logs print staring....");
                List<String> keyList = new ArrayList<>(counter.keySet());
                Collections.sort(keyList);


                while (runBit) {
                    Thread.sleep(60000);

                    if(keyList.size() != counter.keySet().size()){
                        keyList = new ArrayList<>(counter.keySet());
                        Collections.sort(keyList);
                    }

                    if (counter.size() > 0) {
                        System.out.println("--------- MIDDLE COUNT ----------");

                        for (String key : keyList) {
                            System.out.println(key + ": " + counter.get(key));
                        }

                        System.out.println("---------------------------------");
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
        String date = tuple.getStringByField("date2");
        Long sum = tuple.getLongByField("sum");
        this.counter.put(date, sum);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {

    }

    @Override
    public void cleanup() {
        this.printMiddle.runBit = false;

        System.out.println("------- FINAL COUNT -------");
        // 정렬
        List<String> keys = new ArrayList<>(this.counter.keySet());
        Collections.sort(keys);

        for (String key: keys) {
            System.out.println(key + ": "+ this.counter.get(key));
        }
        System.out.println("---------------------------");
    }
}
