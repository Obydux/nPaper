package net.minecraft.server;

import java.util.List;

public class PacketPlayOutSpawnEntityLiving extends Packet {

    private int a;
    private int b;
    private int c;
    private int d;
    private int e;
    private int f;
    private int g;
    private int h;
    private byte i;
    private byte j;
    private byte k;
    private DataWatcher l;
    private List m;

    public PacketPlayOutSpawnEntityLiving() {}

    public PacketPlayOutSpawnEntityLiving(EntityLiving entityliving) {
        this.a = entityliving.getId();
        this.b = (byte) EntityTypes.a(entityliving);
        this.c = MathHelper.floor(entityliving.locX * 32.0D);
        this.d = MathHelper.floor(entityliving.locY * 32.0D);
        this.e = MathHelper.floor(entityliving.locZ * 32.0D);
        this.i = (byte) ((int) (entityliving.yaw * 256.0F / 360.0F));
        this.j = (byte) ((int) (entityliving.pitch * 256.0F / 360.0F));
        this.k = (byte) ((int) (entityliving.aO * 256.0F / 360.0F));
        double d0 = entityliving.motX;
        double d1 = entityliving.motY;
        double d2 = entityliving.motZ;
        double d3 = 3.9D;

        this.f = (int) (MathHelper.a(d0, -d3, d3) * 8000.0D);
        this.g = (int) (MathHelper.a(d1, -d3, d3) * 8000.0D);
        this.h = (int) (MathHelper.a(d2, -d3, d3) * 8000.0D);
        this.l = entityliving.getDataWatcher();
    }

    public void a(PacketDataSerializer packetdataserializer) {
        this.a = packetdataserializer.a();
        this.b = packetdataserializer.readByte() & 255;
        this.c = packetdataserializer.readInt();
        this.d = packetdataserializer.readInt();
        this.e = packetdataserializer.readInt();
        this.i = packetdataserializer.readByte();
        this.j = packetdataserializer.readByte();
        this.k = packetdataserializer.readByte();
        this.f = packetdataserializer.readShort();
        this.g = packetdataserializer.readShort();
        this.h = packetdataserializer.readShort();
        this.m = DataWatcher.b(packetdataserializer);
    }

    public void b(PacketDataSerializer packetdataserializer) {
        packetdataserializer.b(this.a);
        packetdataserializer.writeByte(this.b & 255);
        packetdataserializer.writeInt(this.c);
        packetdataserializer.writeInt(this.d);
        packetdataserializer.writeInt(this.e);
        packetdataserializer.writeByte(this.i);
        packetdataserializer.writeByte(this.j);
        packetdataserializer.writeByte(this.k);
        packetdataserializer.writeShort(this.f);
        packetdataserializer.writeShort(this.g);
        packetdataserializer.writeShort(this.h);
        this.l.a(packetdataserializer, packetdataserializer.version); // Spigot
    }

    public void a(PacketPlayOutListener packetplayoutlistener) {
        packetplayoutlistener.a(this);
    }

    public String b() {
        return String.format("id=%d, type=%d, x=%.2f, y=%.2f, z=%.2f, xd=%.2f, yd=%.2f, zd=%.2f", new Object[] { Integer.valueOf(this.a), Integer.valueOf(this.b), Float.valueOf((float) this.c / 32.0F), Float.valueOf((float) this.d / 32.0F), Float.valueOf((float) this.e / 32.0F), Float.valueOf((float) this.f / 8000.0F), Float.valueOf((float) this.g / 8000.0F), Float.valueOf((float) this.h / 8000.0F)});
    }

    public void handle(PacketListener packetlistener) {
        this.a((PacketPlayOutListener) packetlistener);
    }
}
