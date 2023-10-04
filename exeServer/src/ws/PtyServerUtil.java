package ws;

import com.pty4j.PtyProcess;
import com.pty4j.PtyProcessBuilder;
import com.pty4j.unix.UnixPtyProcess;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class PtyServerUtil {
    private static final Logger log = LoggerFactory.getLogger(PtyServerUtil.class);
    private static InputStream ptyInputStream;
    private static OutputStream ptyOutputStream;
    private static PtyProcess ptyProcess;

    public static void initPty() {
        PtyProcessBuilder ptyProcessBuilder = new PtyProcessBuilder().setCommand(new String[]{"/usr/bin/bash", "-l"});
        HashMap<String, String> env = new HashMap<>(System.getenv());
        env.put("TERM", "xterm-color");
        ptyProcessBuilder.setEnvironment(env).setRedirectErrorStream(true);
        try {
            ptyProcess = ptyProcessBuilder.start();
            UnixPtyProcess upp = (UnixPtyProcess) ptyProcess;
            System.out.println("UnixPtyProcess.pid: " + upp.getPid());//zsh -l 的那个bash进程，即登陆shell进程

            ptyInputStream = ptyProcess.getInputStream();
            ptyOutputStream = ptyProcess.getOutputStream();
        } catch (IOException e) {
            log.error("ptyServer error", e);
        }
        System.out.println("ptyServer initialized successfully!");
    }

    public static void writeToPty(String userInput) {
        try {
            ptyOutputStream.write(userInput.getBytes(StandardCharsets.UTF_8));
            ptyOutputStream.flush();
        } catch (IOException e) {
            log.error("写入ptyServer失败", e);
        }
    }

    public static byte[] readFromPty() {
        ByteBuf buf = Unpooled.buffer();
        try {
            buf.writeBytes(ptyInputStream, 8192);
            byte[] readBytes = new byte[buf.readableBytes()];
            buf.readBytes(readBytes);
            return readBytes;
        } catch (IOException e) {
            log.error("从ptyServer中读取失败", e);
            throw new RuntimeException(e);
        }
    }

    public static boolean isAlive() {
        return ptyProcess.isAlive();
    }

    public static void destroy() {
        ptyProcess.destroy();
    }
}
