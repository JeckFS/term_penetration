/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package ws.server;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class WebSocketFrameHandler extends SimpleChannelInboundHandler<WebSocketFrame> {
    private static final Logger log = LoggerFactory.getLogger(WebSocketFrameHandler.class);
    private static Channel channel;
    private static Map<String, Channel> registeredChannelMap = new HashMap<>();


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {
        // ping and pong frames already handled
        channel = ctx.channel();

        if (frame instanceof TextWebSocketFrame) {
            String request = ((TextWebSocketFrame) frame).text();
            Command cmd = new Gson().fromJson(request, Command.class);
            switch (Command.Ecmd.valueOf(cmd.getCmd())) {
                case REGISTRY://PtyServer端 / frontend
                    registeredChannelMap.put(cmd.getSrc(), channel);
                    Command registerInfo = new Command(Command.Ecmd.INFO.name(), "注册成功");
                    channel.writeAndFlush(new TextWebSocketFrame(registerInfo.toString()));
                    break;
                case CONNECT://前端
                    String hostname = cmd.getDest();
                    Channel hostInner = registeredChannelMap.get(hostname);
                    Command connectResp;
                    if (hostInner != null) {
                        connectResp = new Command(Command.Ecmd.INFO.name(), "连接成功");
                        Command connectInfo = new Command(Command.Ecmd.CONNECTED.name(), hostname);
                        hostInner.writeAndFlush(new TextWebSocketFrame(connectInfo.toString()));
                    } else {
                        connectResp = new Command(Command.Ecmd.INFO.name(), "连接失败");
                    }
                    channel.writeAndFlush(new TextWebSocketFrame(connectResp.toString()));
                    break;
                case CONNECTED:
                case TRANS:
                    Channel peerChannel = registeredChannelMap.get(cmd.getDest());
                    //Command transition = new Command(cmd.getCmd(), cmd.getDest(), cmd.getSrc(), cmd.getData());
                    peerChannel.writeAndFlush(new TextWebSocketFrame(cmd.toString()));
                    break;
                case LIST://前端
                    JsonArray jsonArray = new JsonArray();
                    for (String channelName : registeredChannelMap.keySet()) {
                        jsonArray.add(channelName);
                    }
                    Command listRegCh = new Command(Command.Ecmd.LIST.name(), jsonArray.toString());
                    channel.writeAndFlush(new TextWebSocketFrame(listRegCh.toString()));
                    break;
            }
            System.out.println("write thread: "+Thread.currentThread().getName()+"\tmsg: "+request);
        } else {
            String message = "unsupported frame type: " + frame.getClass().getName();
            System.out.println("Received: " + message);
            throw new UnsupportedOperationException(message);
        }
    }


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
            //Channel upgrade to websocket, remove WebSocketIndexPageHandler.
            ctx.pipeline().remove(WebSocketIndexPageHandler.class);
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
