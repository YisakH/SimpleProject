import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;

import java.util.*;

public class ReportBolt extends BaseRichBolt {

    private HashMap<String, Long> counter = null;

    class MiddlePrint implements Runnable{
        @Override
        public void run() {
            try {
                while(counter.isEmpty())
                    Thread.sleep(10000);

                System.out.println("Logs print staring....");
                List<String> keyList = new ArrayList<String>(counter.keySet());

                Collections.sort(keyList);

                while (true) {
                    Thread.sleep(60000);

                    if(keyList.size() != counter.keySet().size()){
                        keyList = new ArrayList<String>(counter.keySet());
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
        public MiddlePrint(){
            new Thread(this).start();
        }
    }

    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.counter = new HashMap<String, Long>();
        new MiddlePrint();
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
    }

    @Override
    public void execute(Tuple tuple) {
        String lineName = tuple.getStringByField("lineName");
        Long boarding = tuple.getLongByField("boarding");
        this.counter.put(lineName, boarding);
    }

    @Override
    public void cleanup() {
        System.out.println("------- FINAL COUNT -------");
        List<String> keyList = new ArrayList<>(counter.keySet());
        Collections.sort(keyList);

        for (String key: keyList) {
            System.out.println(key + ": "+ this.counter.get(key));
        }
        System.out.println("---------------------------");
    }
}
