package ws.server;

import com.google.gson.Gson;


public class Command {
    private String cmd;
    private String dest;
    private String src;
    private String data;

    public Command() {
    }

    public Command(String cmd, String data) {
        this.cmd = cmd;
        this.data = data;
    }

    public Command(String cmd, String dest, String src, String data) {
        this.cmd = cmd;
        this.dest = dest;
        this.src = src;
        this.data = data;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String getDest() {
        return dest;
    }

    public void setDest(String dest) {
        this.dest = dest;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public enum Ecmd {
        REGISTRY,
        CONNECT,
        INFO,
        CONNECTED,
        TRANS,
        LIST,
        ;
    }
}
