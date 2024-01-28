package net.minecraft.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;

class ServerConnectionChannel extends ChannelInitializer {

    final ServerConnection a;

    ServerConnectionChannel(ServerConnection serverconnection) {
        this.a = serverconnection;
    }

    protected void initChannel(Channel channel) {
        try {
            channel.config().setOption(ChannelOption.IP_TOS, 0x18);
        } catch (ChannelException channelexception) {
            ;
        }

        try {
            channel.config().setOption(ChannelOption.TCP_NODELAY, Boolean.valueOf(true));
        } catch (ChannelException channelexception1) {
            ;
        }

        channel.pipeline().addLast("timeout", new ReadTimeoutHandler(30)).addLast("legacy_query", new LegacyPingHandler(this.a)).addLast("splitter", new PacketSplitter()).addLast("decoder", new PacketDecoder(NetworkManager.h)).addLast("prepender", new PacketPrepender()).addLast("encoder", new PacketEncoder(NetworkManager.h));
        NetworkManager networkmanager = new NetworkManager(false);

        ServerConnection.a(this.a).add(networkmanager);
        channel.pipeline().addLast("packet_handler", networkmanager);
        networkmanager.a((PacketListener) (new HandshakeListener(ServerConnection.b(this.a), networkmanager)));
    }
}
