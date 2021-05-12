import org.apache.kafka.common.metrics.stats.Count;
import org.apache.storm.Config;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.InvalidTopologyException;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;


public class WordCountTopology {
    private static final String KAFKA_SPOUT_ID = "kafka-spout";
    private static final String COUNT_BOLT_ID = "count-bolt";
    private static final String EXTRACT_BOLT_ID = "extract-bolt";
    private static final String REPORT_BOLT_ID = "report-bolt";
    private static final String TOPOLOGY_NAME = "line-count-topology";

    public static void main(String[] args) throws InvalidTopologyException, AuthorizationException, AlreadyAliveException {

        ExtractBolt extractBolt = new ExtractBolt();
        ReportBolt reportBolt = new ReportBolt();
        KafkaSpout kafkaSpout = new KafkaSpout();
        CountBolt countBolt = new CountBolt();

        TopologyBuilder builder = new TopologyBuilder();

        builder.setSpout(KAFKA_SPOUT_ID, kafkaSpout, 1);
        builder.setBolt(EXTRACT_BOLT_ID, extractBolt, 4).shuffleGrouping(KAFKA_SPOUT_ID);
        builder.setBolt(COUNT_BOLT_ID, countBolt, 4).fieldsGrouping(EXTRACT_BOLT_ID, new Fields("lineName"));
        builder.setBolt(REPORT_BOLT_ID, reportBolt).globalGrouping(COUNT_BOLT_ID);


        Config config = new Config();
        config.setNumWorkers(8);

        StormSubmitter.submitTopologyWithProgressBar(
                TOPOLOGY_NAME, config, builder.createTopology()
        );
    }
}
