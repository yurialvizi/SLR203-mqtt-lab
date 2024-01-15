package mqtt;

import java.time.LocalDateTime;

//added external jar: c:\ada\work\lectures\slr203\mqtt\paho\paho-java-maven\org.eclipse.paho.client.mqttv3-1.2.5.jar 

import org.eclipse.paho.client.mqttv3.*;

public class PublishingMqttClient {//synchronous client
	
	public static void main(String[] args) {
		
		String topicPrefix        = "/home/Lyon/sido/";
	    int qos             = 0;
	    // String brokerURI       = "ws://localhost:9001";
	    String brokerURI       = "tcp://137.194.255.43:1883";
	    String clientId     = "myClientID_Pub";
		boolean cleanSession = false;
		boolean retain = false;
	    
		String dht22 = "dht22";
		String sht30 = "sht30";

		String temperatureSufix = "/value";
		String humiditySufix = "/value2";

	    try(
	    	////instantiate a synchronous MQTT Client to connect to the targeted Mqtt Broker
	    	MqttClient mqttClient = new MqttClient(brokerURI, clientId);) {
	    	
	    	////specify the Mqtt Client's connection options
	    	MqttConnectOptions connectOptions = new MqttConnectOptions();
	    	//clean session 
	    	connectOptions.setCleanSession(cleanSession);
	    	//customise other connection options here...
	    	//...
	    	
	    	////connect the mqtt client to the broker
	    	System.out.println("Mqtt Client: Connecting to Mqtt Broker running at: " + brokerURI);
	    	mqttClient.connect(connectOptions);
            System.out.println("Mqtt Client: sucessfully Connected.");
            
            ////publish a message
			
			String topic = topicPrefix + sht30 + humiditySufix;
			for(int i=0; i<60; i++) {
				String temperature = String.valueOf(Math.random()*100);
				System.out.println("Mqtt Client: Publishing message: " + temperature + " to topic: " + topic);
				MqttMessage message = new MqttMessage(temperature.getBytes());//instantiate the message including its content (payload)
				mqttClient.publish(topic, message);//publish the message to a given topic
				System.out.println("Mqtt Client: successfully published the message.");
				Thread.sleep(1000);
			}
            
            ////disconnect the Mqtt Client
            mqttClient.disconnect();
            System.out.println("Mqtt Client: Disconnected.");
	    }
	    catch(MqttException e) {
	    	System.out.println("Mqtt Exception reason: " + e.getReasonCode());
            System.out.println("Mqtt Exception message: " + e.getMessage());
            System.out.println("Mqtt Exception location: " + e.getLocalizedMessage());
            System.out.println("Mqtt Exception cause: " + e.getCause());
            System.out.println("Mqtt Exception reason: " + e);
            e.printStackTrace();
	    } catch (InterruptedException e) {
			e.printStackTrace();
		}
    
	}
    
    

}
