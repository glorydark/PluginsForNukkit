package glorydark.playercountsync;

import cn.nukkit.Server;
import lombok.Data;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class UpdateServerInfoRunnable{
    public static void run() {
        MainClass.all = Server.getInstance().getOnlinePlayers().values().size();
        for(String server: MainClass.servers){
            String[] splits = server.split(":");
            ServerInfo info = null;
            try {
                info = sendUDP(splits[0], Integer.parseInt(splits[1]));
            } catch (SocketException e) {
                //no catch
            }
            if(info != null){
                MainClass.all+=info.online;
            }
        }
        //Server.getInstance().getLogger().warning(MainClass.all+"");
    }

    @Data
    static class ServerInfo{
        private String type;

        private String motd;

        private String version;

        private int protocol;

        private int online;

        private int max;

        private String seed;

        private String subMotd;

        private String defaultGamemode;

        public ServerInfo(String[] args){
            type = args[0];
            motd = args[1];
            protocol = Integer.parseInt(args[2]);
            version = args[3];
            online = Integer.parseInt(args[4]);
            max = Integer.parseInt(args[5]);
            seed = args[6];
            subMotd = args[7];
            defaultGamemode = args[8];
        }
    }

    public static ServerInfo sendUDP(String ip, int port) throws SocketException {
        final byte[] motdData = new byte[]{1, 0, 0, 0, 0, 0, 3, 106, 7, 0, -1, -1, 0, -2, -2, -2, -2, -3, -3, -3, -3, 18, 52, 86, 120, -100, 116, 22, -68};
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket();
            socket.setSoTimeout(5000);
            DatagramPacket packet = new DatagramPacket(Arrays.copyOf(motdData, 1024), 1024, InetAddress.getByName(ip), port);
            socket.send(packet);
            socket.receive(packet);
            return new ServerInfo(new String(packet.getData(), 35, packet.getLength(), StandardCharsets.UTF_8).split(";"));
        } catch (Throwable e) {
            if(socket != null){
                socket.close();
            }
            return null;
        }
    }
}
