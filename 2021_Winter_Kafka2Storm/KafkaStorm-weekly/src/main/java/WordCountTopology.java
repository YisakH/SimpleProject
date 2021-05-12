import org.apache.storm.Config;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.InvalidTopologyException;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;


public class WordCountTopology {
    private static final String KAFKA_SPOUT_ID = "kafka-spout";
    private static final String EXTRACTION_BOLT_ID = "extraction-bolt";
    private static final String COUNT_BOLT_ID = "count-bolt";
    private static final String REPORT_BOLT_ID = "report-bolt";
    private static final String TOPOLOGY_NAME = "weekly-count";

    public static void main(String[] args) throws InvalidTopologyException, AuthorizationException, AlreadyAliveException {

        ExtractionBolt extractionBolt = new ExtractionBolt();
        CountBolt countBolt = new CountBolt();
        ReportBolt reportBolt = new ReportBolt();
        KafkaSpout kafkaSpout = new KafkaSpout();

        TopologyBuilder builder = new TopologyBuilder();
        /*
        //kafka spout
        String zkUrl = "MN:2181,SN01:2181,SN03:2181,SN04:2181,SN05:2181,SN06:2181,SN07:2181,SN08:2181";
        String topic = "KareTopic";
        ZkHosts hosts = new ZkHosts(zkUrl);
        SpoutConfig spoutConfig = new SpoutConfig(hosts, topic, "/"+hosts, UUID.randomUUID().toString());
        spoutConfig.scheme = new SchemeAsMultiScheme(new StringScheme());

        KafkaSpout kafkaSpout = new KafkaSpout(spoutConfig);
         */

        builder.setSpout(KAFKA_SPOUT_ID, kafkaSpout, 4);
        builder.setBolt(EXTRACTION_BOLT_ID, extractionBolt, 4).shuffleGrouping(KAFKA_SPOUT_ID);
        builder.setBolt(COUNT_BOLT_ID, countBolt, 4).fieldsGrouping(EXTRACTION_BOLT_ID, new Fields("week"));
        builder.setBolt(REPORT_BOLT_ID, reportBolt).globalGrouping(COUNT_BOLT_ID);



        /*
        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology(TOPOLOGY_NAME, new Config(), builder.createTopology());
        try { Thread.sleep(1000 * 10); } catch (InterruptedException e) { } // waiting 10s
        cluster.killTopology(TOPOLOGY_NAME);
        cluster.shutdown();
         */

        Config config = new Config();
        config.setNumWorkers(8);

        StormSubmitter.submitTopologyWithProgressBar(
                TOPOLOGY_NAME, config, builder.createTopology()
        );
    }
}
