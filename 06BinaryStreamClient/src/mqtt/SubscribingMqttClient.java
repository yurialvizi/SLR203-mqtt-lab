package mqtt;

import org.eclipse.paho.client.mqttv3.*;

public class SubscribingMqttClient implements MqttCallback {

    public static void main(String[] args) {
        String topic        = "labs/paho-example-topic";
        String brokerURI    = "tcp://localhost:1883";
        String clientId     = "myClientID_Sub";
        int qos             = 0;
        boolean cleanSession = false;
        boolean retain = true;

        // instantiate a synchronous MQTT Client to connect to the targeted Mqtt Broker
        try{
            MqttClient mqttClient = new MqttClient(brokerURI, clientId);   
            mqttClient.setCallback(new SubscribingMqttClient());
            
            // specify the Mqtt Client's connection options
            MqttConnectOptions connectOptions = new MqttConnectOptions();
            // clean session
            connectOptions.setCleanSession(cleanSession);

            // connect the mqtt client to the broker
            System.out.println("Mqtt Client: Connecting to Mqtt Broker running at: " + brokerURI);
            mqttClient.connect(connectOptions);
            System.out.println("Mqtt Client: sucessfully Connected.");

            mqttClient.subscribe(topic, qos);
            System.out.println("Mqtt Client: sucessfully subscribed.");
        } catch (MqttException e) {
            System.out.println("Mqtt Exception reason: " + e.getReasonCode());
            System.out.println("Mqtt Exception message: " + e.getMessage());
            System.out.println("Mqtt Exception location: " + e.getLocalizedMessage());
            System.out.println("Mqtt Exception cause: " + e.getCause());
            System.out.println("Mqtt Exception reason: " + e);
            e.printStackTrace();
        }
    }

    @Override
    public void connectionLost(Throwable cause) {
        System.out.println("Mqtt Client: Connection to Mqtt Broker lost!");
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        System.out.println("Mqtt Client: Message received:");
        System.out.println("\tTopic: " + topic);
        System.out.println("\tMessage: " + new String(message.getPayload()));
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        System.out.println("Mqtt Client: Message delivery complete");
    }
}
