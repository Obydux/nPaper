package net.minecraft.server;

import java.util.List;

public class PacketPlayOutEntityMetadata extends Packet {

    private int a;
    private List<WatchableObject> b;
    private boolean foundHealth = false;

    public PacketPlayOutEntityMetadata() {}
    
    // SportPaper start
 	public PacketPlayOutEntityMetadata(int i, List<WatchableObject> list) {
 		this.a = i;
 		this.b = list;
 	}
 	// SportPaper end


    public PacketPlayOutEntityMetadata(int i, DataWatcher datawatcher, boolean flag) {
        this.a = i;
        if (flag) {
            this.b = datawatcher.c();
        } else {
            this.b = datawatcher.b();
        }
    }

    public PacketPlayOutEntityMetadata obfuscateHealth() {
        final Iterator<WatchableObject> iter = b.iterator();
        if (this.foundHealth) {
        	this.foundHealth = false;
        }

        while (iter.hasNext()) {
            final WatchableObject watchable = iter.next();
            if (watchable.a() == 6 && (float) watchable.b() > 0) {
                iter.remove();
                this.foundHealth = true;
                break;
            }
        }

        if (this.foundHealth) {
            b.add(new WatchableObject(3, 6, 1.0F));
        }
        return this;
    }
    
    public boolean didFindHealth() {
        return this.foundHealth;
    }

    public void a(PacketDataSerializer packetdataserializer) {
        this.a = packetdataserializer.readInt();
        this.b = DataWatcher.b(packetdataserializer);
    }

    public void b(PacketDataSerializer packetdataserializer) {
        // Spigot start - protocol patch
        if ( packetdataserializer.version < 16 )
        {
            packetdataserializer.writeInt( this.a );
        } else
        {
            packetdataserializer.b( a );
        }
        DataWatcher.a(this.b, packetdataserializer, packetdataserializer.version);
        // Spigot end
    }

    public void a(PacketPlayOutListener packetplayoutlistener) {
        packetplayoutlistener.a(this);
    }

    public void handle(PacketListener packetlistener) {
        this.a((PacketPlayOutListener) packetlistener);
    }
}
