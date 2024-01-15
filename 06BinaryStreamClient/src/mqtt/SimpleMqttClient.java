package mqtt;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.time.LocalDateTime;

public class SimpleMqttClient {

    public static void main(String[] args) {
        String brokerAddress = "127.0.0.1"; // Substitua pelo endereço do seu broker MQTT
        int port = 1883; // Porta padrão MQTT

        try {
            // Conecte-se ao broker MQTT
            Socket socket = new Socket(brokerAddress, port);
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());

            // Enviar mensagem CONNECT
            System.out.println(LocalDateTime.now() + ": Enviando mensagem CONNECT...");
            byte[] connectMessage = buildConnectMessage();
            outputStream.write(connectMessage);
            // outputStream.flush();

            // Receber mensagem CONNACK
            byte[] connackMessage = new byte[4]; // Tamanho fixo de 4 bytes para CONNACK
            inputStream.readFully(connackMessage);

            // Verificar se a mensagem é um CONNACK
            if (connackMessage[0] == (byte) 0x20 && connackMessage[3] == (byte) 0x00) {
                System.out.println("CONNACK recebido com sucesso.");
            } else {
                System.out.println("Falha ao receber CONNACK.");
            }

            // Feche a conexão
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static byte[] buildConnectMessage() {
        // Construir a mensagem CONNECT em bytes
        byte[] connectMessage = new byte[21];
        connectMessage[0] = (byte) 0x10; // Tipo de mensagem CONNECT
        connectMessage[1] = (byte) 0x13; // Tamanho da mensagem (12 bytes)
        connectMessage[2] = (byte) 0x00; // Flags (sem flags)
        connectMessage[3] = (byte) 0x04; // Protocolo MQTT versão 3.1.1
        connectMessage[4] = (byte) 0x4D; // Flags de Conexão (sem flags)
        connectMessage[5] = (byte) 0x51; // Tempo de manutenção MSB
        connectMessage[6] = (byte) 0x54; // Tempo de manutenção LSB (60 segundos)
        connectMessage[7] = (byte) 0x54; // ID do cliente MSB
        connectMessage[8] = (byte) 0x04; // ID do cliente LSB (tamanho 4)
        connectMessage[9] = (byte) 0x02; // ID do cliente LSB (tamanho 4)
        connectMessage[10] = (byte) 0x00; // ID do cliente LSB (tamanho 4)
        connectMessage[11] = (byte) 0x3C; // ID do cliente LSB (tamanho 4)
        connectMessage[12] = (byte) 0x00; // ID do cliente LSB (tamanho 4)
        connectMessage[13] = (byte) 0x07; // ID do cliente LSB (tamanho 4)
        connectMessage[14] = 'P'; // ID do cliente LSB (tamanho 4)
        connectMessage[15] = 'Y'; // ID do cliente LSB (tamanho 4)
        connectMessage[16] = 'T'; // ID do cliente LSB (tamanho 4)
        connectMessage[17] = 'H'; // ID do cliente LSB (tamanho 4)
        connectMessage[18] = 'O'; // ID do cliente LSB (tamanho 4)
        connectMessage[19] = 'N'; // ID do cliente LSB (tamanho 4)
        connectMessage[20] = '1'; // ID do cliente LSB (tamanho 4)

        return connectMessage;
    }
}