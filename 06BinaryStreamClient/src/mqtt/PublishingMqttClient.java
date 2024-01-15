package mqtt;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.time.LocalDateTime;

//added external jar: c:\ada\work\lectures\slr203\mqtt\paho\paho-java-maven\org.eclipse.paho.client.mqttv3-1.2.5.jar 

// import org.eclipse.paho.client.mqttv3.*;

public class PublishingMqttClient {//synchronous client
	private static final byte   CONNECT = 0x10;
	private static final byte   CONNACK = 0x20;
	private static final byte   PUBLISH = 0x30;
	private static final byte[] LENGTH_OF_PROTOCOL_NAME = {0x00, 0x04};
	private static final byte[] MQTT = {'M', 'Q', 'T', 'T'};
	private static final byte   MQTT_VERSION = 0x04;
	private static final byte[] KEEP_ALIVE = {0x00, 0x3C};
	
	public static void main(String[] args) {
	    String brokerURI       = "127.0.0.1";
	    int brokerPort       = 1883;
		String topic        = "labs/paho-example-topic";
	    String clientID     = "mySocketClient";
	    int qos             = 0;
		boolean cleanSession = true;
		boolean retain = false;
		String message = "Hello World!";
		
		try {
			Socket socket = new Socket(brokerURI, brokerPort);
			DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
			DataInputStream inputStream = new DataInputStream(socket.getInputStream());

			connect(outputStream, clientID, qos, cleanSession);

			if (connectionAccepted(inputStream)) {
				publish(outputStream, topic, message, qos, retain);
			}
			
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
            System.out.println("Socket Exception message: " + e.getMessage());
            System.out.println("Socket Exception location: " + e.getLocalizedMessage());
            System.out.println("Socket Exception cause: " + e.getCause());
            System.out.println("Socket Exception reason: " + e);
		}



	}

	private static int connect(DataOutputStream outputStream, String clientID, int qos, boolean cleanSession) {
		// byte[] connectMsg = new byte[21];
		// connectMsg[0] = (byte) 0x10; // Tipo de mensagem CONNECT
		// connectMsg[1] = (byte) 0x13; // Tamanho da mensagem (12 bytes)
		// connectMsg[2] = (byte) 0x00; // Flags (sem flags)
		// connectMsg[3] = (byte) 0x04; // Protocolo MQTT versão 3.1.1
		// connectMsg[4] = (byte) 0x4D; // Flags de Conexão (sem flags)
		// connectMsg[5] = (byte) 0x51; // Tempo de manutenção MSB
		// connectMsg[6] = (byte) 0x54; // Tempo de manutenção LSB (60 segundos)
		// connectMsg[7] = (byte) 0x54; // ID do cliente MSB
		// connectMsg[8] = (byte) 0x04; // ID do cliente LSB (tamanho 4)
		// connectMsg[9] = (byte) 0x02; // ID do cliente LSB (tamanho 4)
		// connectMsg[10] = (byte) 0x00; // ID do cliente LSB (tamanho 4)
		// connectMsg[11] = (byte) 0x3C; // ID do cliente LSB (tamanho 4)
		// connectMsg[12] = (byte) 0x00; // ID do cliente LSB (tamanho 4)
		// connectMsg[13] = (byte) 0x07; // ID do cliente LSB (tamanho 4)
		// connectMsg[14] = 'P'; // ID do cliente LSB (tamanho 4)
		// connectMsg[15] = 'Y'; // ID do cliente LSB (tamanho 4)
		// connectMsg[16] = 'T'; // ID do cliente LSB (tamanho 4)
		// connectMsg[17] = 'H'; // ID do cliente LSB (tamanho 4)
		// connectMsg[18] = 'O'; // ID do cliente LSB (tamanho 4)
		// connectMsg[19] = 'N'; // ID do cliente LSB (tamanho 4)
		// connectMsg[20] = '1'; // ID do cliente LSB (tamanho 4)

		
		int remainingLength = clientID.length() + 12;
		byte remainingLengthByte = encodeLengthOneByte(remainingLength);
		byte connectFlags = 0b00000000;

        connectFlags |= ((qos & 0b11) << 3); // Define os bits 3 e 4 de acordo com o valor de qos (0 a 2)
        if (cleanSession) {
            connectFlags |= (1 << 1); // Define o bit 1 como 1
        }

		ByteArrayOutputStream connectMessage = new ByteArrayOutputStream();

		try {
			connectMessage.write(CONNECT);
			connectMessage.write(remainingLengthByte);
			connectMessage.write(LENGTH_OF_PROTOCOL_NAME); // 2 bytes
			connectMessage.write(MQTT); // 4 bytes
			connectMessage.write(MQTT_VERSION); // 1 byte
			connectMessage.write(connectFlags); // 1 byte
			connectMessage.write(KEEP_ALIVE); // 2 bytes
			connectMessage.write(encodeLengthTwoBytes(clientID.length())); // 2 byte
			connectMessage.write(clientID.getBytes(), 0, clientID.length());
		} catch (IOException e) {
			System.out.println(LocalDateTime.now() + ". Falha ao criar CONNECT.");
			e.printStackTrace();
			return -1;
		}

		byte[] connectMessageBytes = connectMessage.toByteArray();
		String packetString = packetToString(connectMessageBytes);

		try {
			System.out.println(LocalDateTime.now()+". Sending CONNECT message...");
			System.out.println("Sending packet: " + packetString);
			outputStream.write(connectMessageBytes);
		} catch (IOException e) {
			System.out.println(LocalDateTime.now() + ". Falha ao enviar CONNECT.");
			e.printStackTrace();
			return -1;
		}

		return 0;
	}

	private static void publish(DataOutputStream outputStream, String topic, String message, int qos, boolean retain) {
		byte fixedHeader = (byte) (PUBLISH | ((qos & 0x03) << 1) | (retain ? 0x01 : 0x00));
		byte[] topicBytes = topic.getBytes();
		byte[] payload = message.getBytes();
		int remainingLength = 2 + topicBytes.length + payload.length; // 2 bytes for topic length
		byte[] topicLengthByte = encodeLengthTwoBytes(topicBytes.length);
		byte remainingLengthByte = encodeLengthOneByte(remainingLength);

		ByteArrayOutputStream publishMessage = new ByteArrayOutputStream();
		try {
			publishMessage.write(fixedHeader);
			publishMessage.write(remainingLengthByte);
			publishMessage.write(topicLengthByte);
			publishMessage.write(topicBytes, 0, topicBytes.length);
			publishMessage.write(payload, 0, payload.length);
		} catch (IOException e) {
			System.out.println(LocalDateTime.now() + ". Fail creating PUBLISH message.");
			e.printStackTrace();
		}

		byte[] publishMessageBytes = publishMessage.toByteArray();

		System.out.println(LocalDateTime.now() + ". Publishing message: " + message + " to topic: " + topic);
		System.out.println("Packet: " + packetToString(publishMessageBytes));
		System.out.println("Packet ASCII: " + packetToASCIIString(publishMessageBytes));

		try {
			outputStream.write(publishMessageBytes);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
    
    private static boolean connectionAccepted(DataInputStream inputStream) throws IOException {
        byte messageType = inputStream.readByte();

        // Read the variable length of the message (one byte at a time)
        int remainingLength = 0;
        int multiplier = 1;
        int digit;
        do {
            digit = inputStream.readUnsignedByte();
            remainingLength += (digit & 127) * multiplier;
            multiplier *= 128;
        } while ((digit & 128) != 0);

        // Read the bytes of the message payload
        byte[] payload = new byte[remainingLength];
        inputStream.readFully(payload);

		byte[] packet = new byte[2 + remainingLength];
		packet[0] = messageType;
		packet[1] = (byte) remainingLength;
		for (int i = 0; i < remainingLength; i++) {
			packet[i + 2] = payload[i];
		}

        String packetString = packetToString(packet);

        // Print the received packet
        System.out.println("Received packet: " + packetString);

		if (packet[0] == CONNACK && packet[3] == (byte) 0x00) {
			System.out.println("CONNACK successfully received.");
			return true;
		} else {
			System.out.println("Fail receiving CONNACK.");
			return false;
		}
    }

	public static byte encodeLengthOneByte(int length) {
        return (byte) (length & 0xFF);
    }

	public static byte[] encodeLengthTwoBytes(int length) {
		byte[] byteArray = new byte[2];
        byteArray[0] = (byte) ((length >> 8) & 0xFF); // Most significant byte
		byteArray[1] = (byte) (length & 0xFF);        // Least significant byte
        return byteArray;
	}

	public static String packetToString(byte[] packet) {
		String packetString = "";
		for (byte b : packet) {
			packetString +=   String.format("%02X", b) + " ";
		}
		return packetString;
	}

	public static String packetToASCIIString(byte[] packet) {
		String packetString = "";
		for (byte b : packet) {
			packetString +=   String.format("%c", b) + " ";
		}
		return packetString;
	}

}
