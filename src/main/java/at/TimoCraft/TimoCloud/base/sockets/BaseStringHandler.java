package at.TimoCraft.TimoCloud.base.sockets;

import at.TimoCraft.TimoCloud.base.Base;
import at.TimoCraft.TimoCloud.bukkit.Main;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 * Created by Timo on 29.12.16.
 */
public class BaseStringHandler extends SimpleChannelInboundHandler<String> {

    String remaining = "";
    String parsed = "";
    int open = 0;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String message) {
        Main.getInstance().getSocketClientHandler().setChannel(ctx.channel());
        remaining = remaining + message;
        read();
    }

    public void read() {
        for (String c : remaining.split("")) {
            parsed = parsed + c;
            remaining = remaining.substring(1);
            if (c.equals("{")) {
                open++;
            }
            if (c.equals("}")) {
                open--;
                if (open == 0) {
                    handleJSON((JSONObject) JSONValue.parse(parsed), parsed);
                    parsed = "";
                }
            }
        }
    }

    public void handleJSON(JSONObject json, String message) {
        if (json == null) {
            Main.log("Error while parsing json: " + message);
            return;
        }
        String server = (String) json.get("server");
        String type = (String) json.get("type");
        String data = (String) json.get("data");
        switch (type) {
            case "STARTSERVER":
                int port = (Integer) json.get("port");
                int ram = (Integer) json.get("ram");
                Base.getInstance().getServerManager().startServer(server, port, ram);
                break;
            default:
                Main.log("Error: Could not categorize json message: " + message);
        }
    }

}