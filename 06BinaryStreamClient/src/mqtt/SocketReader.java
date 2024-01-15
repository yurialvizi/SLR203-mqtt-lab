package mqtt;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class SocketReader extends Thread {

    private Socket socket;

    public SocketReader(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            DataInputStream in = new DataInputStream(socket.getInputStream());
            processMessage(in);
            // byte[] buffer = new byte[1024];
            // int length = in.read(buffer);

            // // Display the contents of the received CONNACK message
            // for (int i = 0; i < length; i++) {
            //     System.out.printf("%02X ", buffer[i]);
            //     System.out.println();
            // }


            // byte message = in.readByte();
            // while (message != -1) {
            //     System.out.println("Received response: " + in.readByte());
            //     message = in.readByte();
            // }
            // System.out.println("Input stream: " + in);
            // System.out.println("Input stream: " + in.readUTF());


            // String response;
            // int first = in.readUnsignedByte();
            // System.out.println("Received response: " + first);
            // byte type = (byte) ((first >>> 4) & 0x0F);
            // System.out.println("Received response: " + type);

            // while ((response = in.readByte()) != null) {
            //     System.out.println("Received response: " + response);

            //     // Process the response as needed
            // }

        } catch (EOFException e) {
            System.out.println("Socket Reader: EOF: " + e);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Socket Reader: Exception: " + e);
            System.out.println("Socket Reader. Exception message: " + e.getMessage());
            System.out.println("Socket Reader. Exception location: " + e.getLocalizedMessage());
            System.out.println("Socket Reader. Exception cause: " + e.getCause());
            System.out.println("Socket Reader. Exception reason: " + e);
        // } catch (InterruptedException e) {
        //     // TODO Auto-generated catch block
        //     e.printStackTrace();
        }
    }

    private void processMessage(DataInputStream inputStream) throws IOException {
        byte messageType = inputStream.readByte();

        // Lê o tamanho variável da mensagem (um byte de cada vez)
        int remainingLength = 0;
        int multiplier = 1;
        int digit;
        do {
            digit = inputStream.readUnsignedByte();
            remainingLength += (digit & 127) * multiplier;
            multiplier *= 128;
        } while ((digit & 128) != 0);

        // Lê os bytes da carga útil da mensagem
        byte[] payload = new byte[remainingLength];
        inputStream.readFully(payload);

        String packet = String.format("%02X", messageType) + " " + String.format("%02X", remainingLength);
        for (byte b : payload) {
            packet +=  " " + String.format("%02X", b);
        }

        // Imprime informações sobre a mensagem recebida
        System.out.println("Packet: " + packet);
    }

}