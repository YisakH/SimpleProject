import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class SimpleProducer {
    private static String TOPIC_NAME = "SubwayLog";
    private static String BOOTSTRAP_SERVERS = "114.70.235.43:19092,114.70.235.43:19093,114.70.235.43:19094,"
        + "114.70.235.43:19095,114.70.235.43:19096,114.70.235.43:19097,114.70.235.43:19098,114.70.235.43:19099";

    public static void main(String[] args) throws IOException {
        Properties configs = new Properties();
        configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);

        configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        KafkaProducer<String, String> producer = new KafkaProducer<>(configs);

        BufferedReader br = new BufferedReader(new FileReader("SUBWAY.csv"));
        String line;

        // 첫 행은 무시함
        br.readLine();

        int cnt = 1;
        while((line=br.readLine())!=null) {
            JSONObject jobject = new JSONObject();
            line = line.replaceAll("\"", "");

            String[] subdata = line.split(",");
            jobject.put("date", subdata[0]);
            jobject.put("line name", subdata[1]);
            jobject.put("station name", subdata[2]);
            jobject.put("board", subdata[3]);
            jobject.put("alight", subdata[4]);


            ProducerRecord<String, String> record = new ProducerRecord<String, String>(TOPIC_NAME, jobject.toString());

            try{
                producer.send(record);

                System.out.println(cnt++ + "번째 데이터 전송중..");
                Thread.sleep(10);

            }catch (Exception e){
                System.out.println(e);
            }
        }

        producer.flush();
        producer.close();
    }
}