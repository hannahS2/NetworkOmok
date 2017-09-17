import processing.core.PApplet;
import processing.event.MouseEvent;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Omok extends PApplet {
    private static final int WIDTH = 600;
    private static final int ROW = 16;
    private static final int MARGIN = 40;
    private static final int HALF_MARGIN = 20;
    private static final int RADIUS = 35;
    private static final int INTERVAL = WIDTH / (ROW - 1);

    private static int[][] stones = new int[ROW][ROW];

    private static int user = 1;
    private static InputStream is;
    private static OutputStream os;
    private static byte[] data;

    public static void main(String[] args) {
        PApplet.main("Omok");
        data = new byte[1024];
        try {
            InetSocketAddress socketAddress = new InetSocketAddress("192.168.11.31", 8888);
            Socket socket = new Socket();
            socket.connect(socketAddress);
            System.out.println("연결");

            is = socket.getInputStream();
            os = socket.getOutputStream();

            byte[] data = new byte[1024];

            while (true) {
                InputStream is = socket.getInputStream();

                int len = is.read(data);
                if(len == -1) {
                    break;
                }

                String str = new String(data, 0 ,len);

                if(str.contains("Start")) {
                    System.out.println("Game Start");
                    System.out.println("Your color is " + str.split(":")[1]);
                    user = Integer.parseInt(str.split(":")[1]);
                } else if(str.contains("put")) {
                    String[] stoneInfo = str.split(" ");

                    stones[Integer.parseInt(stoneInfo[1])][Integer.parseInt(stoneInfo[2])] = Integer.parseInt(stoneInfo[3]);

                } else if(str.contains("WIN")) {
                    System.out.println(str.split(" ")[0]+str.split(" ")[1]);
                }
            }

            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void settings() {
        this.size(WIDTH + MARGIN, WIDTH + MARGIN);
    }

    @Override
    public void setup() {
        this.background(248, 196, 126);
        drawMap();
    }

    @Override
    public void draw() {
        drawStone();
    }

    private void drawMap() {
        for (int i = 0; i < ROW; i++) {
            line(HALF_MARGIN, HALF_MARGIN + i * INTERVAL, WIDTH + HALF_MARGIN, HALF_MARGIN + i * INTERVAL);
            line(HALF_MARGIN + i * INTERVAL, HALF_MARGIN, HALF_MARGIN + i * INTERVAL, WIDTH + HALF_MARGIN);
        }
    }

    private void drawStone() {
        for (int i = 0; i < stones.length; i++) {
            for (int j = 0; j < stones[i].length; j++) {
                if (stones[i][j] == 0) {
                    continue;
                }

                int color = stones[i][j] == 1 ? 0 : 255;
                fill(color);
                ellipse(i * INTERVAL + HALF_MARGIN, j * INTERVAL + HALF_MARGIN, RADIUS, RADIUS);
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent event) {

        int posX = mouseX / INTERVAL;
        int posY = mouseY / INTERVAL;
        System.out.println(posX + " " + posY);

        String str = String.valueOf("put "+posX + " " + posY + " " + user);
        try {
            os.write(str.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
