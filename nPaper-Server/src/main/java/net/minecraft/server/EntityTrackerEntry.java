package net.minecraft.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
// CraftBukkit start
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerVelocityEvent;
// CraftBukkit end
import org.github.paperspigot.PaperSpigotConfig;

public class EntityTrackerEntry {

    private static final Logger p = LogManager.getLogger();
    public Entity tracker;
    public int b;
    public int c;
    public int xLoc;
    public int yLoc;
    public int zLoc;
    public int yRot;
    public int xRot;
    public int i;
    public double j;
    public double k;
    public double l;
    public int m;
    private double q;
    private double r;
    private double s;
    private boolean isMoving;
    private boolean u;
    private int v;
    private Entity w;
    private boolean x;
    public boolean n;
    public final Map<EntityPlayer, Boolean> trackedPlayerMap = new java.util.HashMap<EntityPlayer, Boolean>();
    public final Set<EntityPlayer> trackedPlayers = trackedPlayerMap.keySet();

    public EntityTrackerEntry(Entity entity, int i, int j, boolean flag) {
        this.tracker = entity;
        this.b = i;
        this.c = j;
        this.u = flag;
        this.xLoc = (int) Math.floor(entity.locX * 32.0D);
        this.yLoc = (int) Math.floor(entity.locY * 32.0D);
        this.zLoc = (int) Math.floor(entity.locZ * 32.0D);
        this.yRot = MathHelper.d(entity.yaw * 256.0F / 360.0F);
        this.xRot = MathHelper.d(entity.pitch * 256.0F / 360.0F);
        this.i = MathHelper.d(entity.getHeadRotation() * 256.0F / 360.0F);
    }

    public boolean equals(Object object) {
        return object instanceof EntityTrackerEntry ? ((EntityTrackerEntry) object).tracker.getId() == this.tracker.getId() : false;
    }

    public int hashCode() {
        return this.tracker.getId();
    }

    public void track(List list) {
        this.n = false;
        if (!this.isMoving || this.tracker.e(this.q, this.r, this.s) > 16.0D) {
            this.q = this.tracker.locX;
            this.r = this.tracker.locY;
            this.s = this.tracker.locZ;
            this.isMoving = true;
            this.n = true;
            this.scanPlayers(list);
        }

        if (this.w != this.tracker.vehicle || this.tracker.vehicle != null && this.m % 60 == 0) {
            this.w = this.tracker.vehicle;
            this.broadcast(new PacketPlayOutAttachEntity(0, this.tracker, this.tracker.vehicle));
        }

        if (this.tracker instanceof EntityItemFrame /*&& this.m % 10 == 0*/) { // CraftBukkit - Moved below, should always enter this block
            EntityItemFrame i3 = (EntityItemFrame) this.tracker;
            ItemStack i4 = i3.getItem();

            if (this.m % 10 == 0 && i4 != null && i4.getItem() instanceof ItemWorldMap) { // CraftBukkit - Moved this.m % 10 logic here so item frames do not enter the other blocks
                WorldMap i6 = Items.MAP.getSavedMap(i4, this.tracker.world);
                Iterator i7 = this.trackedPlayers.iterator(); // CraftBukkit

                while (i7.hasNext()) {
                    EntityHuman i8 = (EntityHuman) i7.next();
                    EntityPlayer i9 = (EntityPlayer) i8;

                    i6.a(i9, i4);
                    Packet j0 = Items.MAP.c(i4, this.tracker.world, i9);

                    if (j0 != null) {
                        i9.playerConnection.sendPacket(j0);
                    }
                }
            }

            this.b();
        } else if (this.m % this.c == 0 || this.tracker.al || this.tracker.getDataWatcher().a()) {
            int i;
            int j;

            if (this.tracker.vehicle == null) {
                ++this.v;
                i = (int) Math.floor(this.tracker.locX * 32.0D);
                j = (int) Math.floor(this.tracker.locY * 32.0D);
                int k = (int) Math.floor(this.tracker.locZ * 32.0D);
                int l = MathHelper.d(this.tracker.yaw * 256.0F / 360.0F);
                int i1 = MathHelper.d(this.tracker.pitch * 256.0F / 360.0F);
                int j1 = i - this.xLoc;
                int k1 = j - this.yLoc;
                int l1 = k - this.zLoc;
                Packet packet = null;
                // Rinny moved-down
                //boolean flag = Math.abs(j1) >= 4 || Math.abs(k1) >= 4 || Math.abs(l1) >= 4 || this.m % 60 == 0;
                //boolean flag1 = Math.abs(l - this.yRot) >= 4 || Math.abs(i1 - this.xRot) >= 4;
                // Rinny stop

                if (this.m > 0 || this.tracker instanceof EntityArrow) { // PaperSpigot - Move up
                    // Rinny start -- moved
                    boolean flag = Math.abs(j1) >= 4 || Math.abs(k1) >= 4 || Math.abs(l1) >= 4 || this.m % 60 == 0;
                    boolean flag1 = Math.abs(l - this.yRot) >= 4 || Math.abs(i1 - this.xRot) >= 4;
                    // Rinny end

                    // CraftBukkit start - Code moved from below
                    if (flag) {
                        this.xLoc = i;
                        this.yLoc = j;
                        this.zLoc = k;
                    }

                    if (flag1) {
                        this.yRot = l;
                        this.xRot = i1;
                    }
                    // CraftBukkit end

                    if (j1 >= -128 && j1 < 128 && k1 >= -128 && k1 < 128 && l1 >= -128 && l1 < 128 && this.v <= 400 && !this.x) {
                        if (flag && flag1) {
                            packet = new PacketPlayOutRelEntityMoveLook(this.tracker.getId(), (byte) j1, (byte) k1, (byte) l1, (byte) l, (byte) i1, tracker.onGround); // Spigot - protocol patch
                        } else if (flag) {
                            packet = new PacketPlayOutRelEntityMove(this.tracker.getId(), (byte) j1, (byte) k1, (byte) l1, tracker.onGround); // Spigot - protocol patch
                        } else if (flag1) {
                            packet = new PacketPlayOutEntityLook(this.tracker.getId(), (byte) l, (byte) i1, tracker.onGround); // Spigot - protocol patch
                        }
                    } else {
                        this.v = 0;
                        // CraftBukkit start - Refresh list of who can see a player before sending teleport packet
                        if (this.tracker instanceof EntityPlayer) {
                            this.scanPlayers(this.tracker.world.players); // scan all players of the world (that's fix invisibility after teleportation)
                        }
                        // CraftBukkit end
                        packet = new PacketPlayOutEntityTeleport(this.tracker.getId(), i, j, k, (byte) l, (byte) i1, tracker.onGround, tracker instanceof EntityFallingBlock || tracker instanceof EntityTNTPrimed); // Spigot - protocol patch
                    }
                }

                if (this.u) {
                    double d0 = this.tracker.motX - this.j;
                    double d1 = this.tracker.motY - this.k;
                    double d2 = this.tracker.motZ - this.l;
                    double d3 = 0.02D;
                    double d4 = d0 * d0 + d1 * d1 + d2 * d2;

                    if (d4 > d3 * d3 || d4 > 0.0D && this.tracker.motX == 0.0D && this.tracker.motY == 0.0D && this.tracker.motZ == 0.0D) {
                        this.j = this.tracker.motX;
                        this.k = this.tracker.motY;
                        this.l = this.tracker.motZ;
                        this.broadcast(new PacketPlayOutEntityVelocity(this.tracker.getId(), this.j, this.k, this.l));
                    }
                }

                if (packet != null) {
                    // PaperSpigot start - ensure fresh viewers get an absolute position on their first update,
                    // since we can't be certain what position they received in the spawn packet.
                    if (packet instanceof PacketPlayOutEntityTeleport) {
                        this.broadcast(packet);
                    } else {
                        PacketPlayOutEntityTeleport teleportPacket = null;

                        for(Map.Entry<EntityPlayer, Boolean> viewer : trackedPlayerMap.entrySet()) {
                            if(viewer.getValue()) {
                                viewer.setValue(false);
                                if(teleportPacket == null) {
                                    teleportPacket = new PacketPlayOutEntityTeleport(this.tracker);
                                }
                                this.sendPlayerPacket(viewer.getKey(), teleportPacket);
                            } else {
                                this.sendPlayerPacket(viewer.getKey(), packet);
                            }
                        }
                    }
                }

                this.b();
                /* CraftBukkit start - Code moved up
                if (flag) {
                    this.xLoc = i;
                    this.yLoc = j;
                    this.zLoc = k;
                }

                if (flag1) {
                    this.yRot = l;
                    this.xRot = i1;
                }
                // CraftBukkit end */

                this.x = false;
            } else {
                i = MathHelper.d(this.tracker.yaw * 256.0F / 360.0F);
                j = MathHelper.d(this.tracker.pitch * 256.0F / 360.0F);
                boolean flag2 = Math.abs(i - this.yRot) >= 4 || Math.abs(j - this.xRot) >= 4;

                if (flag2) {
                    this.broadcast(new PacketPlayOutEntityLook(this.tracker.getId(), (byte) i, (byte) j, tracker.onGround)); // Spigot - protocol patch
                    this.yRot = i;
                    this.xRot = j;
                }

                this.xLoc = (int) Math.floor(this.tracker.locX * 32.0D);
                this.yLoc = (int) Math.floor(this.tracker.locY * 32.0D);
                this.zLoc = (int) Math.floor(this.tracker.locZ * 32.0D);
                this.b();
                this.x = true;
            }

            i = MathHelper.d(this.tracker.getHeadRotation() * 256.0F / 360.0F);
            if (Math.abs(i - this.i) >= 4) {
                this.broadcast(new PacketPlayOutEntityHeadRotation(this.tracker, (byte) i));
                this.i = i;
            }

            this.tracker.al = false;
        }

        ++this.m;
        if (this.tracker.velocityChanged) {
            // CraftBukkit start - Create PlayerVelocity event
            boolean cancelled = false;

            if (this.tracker instanceof EntityPlayer) {
                Player player = (Player) this.tracker.getBukkitEntity();
                org.bukkit.util.Vector velocity = player.getVelocity();

                PlayerVelocityEvent event = new PlayerVelocityEvent(player, velocity);
                this.tracker.world.getServer().getPluginManager().callEvent(event);

                if (event.isCancelled()) {
                    cancelled = true;
                } else if (!velocity.equals(event.getVelocity())) {
                    player.setVelocity(velocity);
                }
            }

            if (!cancelled) {
                this.broadcastIncludingSelf((Packet) (new PacketPlayOutEntityVelocity(this.tracker)));
            }
            // CraftBukkit end

            this.tracker.velocityChanged = false;
        }
    }

    private void b() {
        DataWatcher datawatcher = this.tracker.getDataWatcher();

        if (datawatcher.a()) {
        	final List<WatchableObject> changedMetadata = datawatcher.b();
        	if (PaperSpigotConfig.obfuscatePlayerHealth && this.tracker.isAlive() && (this.tracker instanceof EntityPlayer)) {
        		final PacketPlayOutEntityMetadata metadataPacket = new PacketPlayOutEntityMetadata(this.tracker.getId(), new ArrayList<WatchableObject>(changedMetadata)).obfuscateHealth();
                if (!metadataPacket.didFindHealth() || 1 < metadataPacket.getMetadata().size()) {
                	this.broadcast(metadataPacket);
                }
        	} else {
        		this.broadcast(new PacketPlayOutEntityMetadata(this.tracker.getId(), changedMetadata));
        	}
        	
        	if (this.tracker instanceof EntityPlayer) {
                ((EntityPlayer) this.tracker).playerConnection.sendPacket(new PacketPlayOutEntityMetadata(this.tracker.getId(), changedMetadata));
            }
        }

        if (this.tracker instanceof EntityLiving) {
            AttributeMapServer attributemapserver = (AttributeMapServer) ((EntityLiving) this.tracker).getAttributeMap();
            Set set = attributemapserver.getAttributes();

            if (!set.isEmpty()) {
                // CraftBukkit start - Send scaled max health
                if (this.tracker instanceof EntityPlayer) {
                    ((EntityPlayer) this.tracker).getBukkitEntity().injectScaledMaxHealth(set, false);
                }
                // CraftBukkit end
                this.broadcastIncludingSelf(new PacketPlayOutUpdateAttributes(this.tracker.getId(), set));
            }

            set.clear();
        }
    }

    public void broadcast(Packet packet) {
        Iterator iterator = this.trackedPlayers.iterator();

        while (iterator.hasNext()) {
            EntityPlayer entityplayer = (EntityPlayer) iterator.next();

            entityplayer.playerConnection.sendPacket(packet);
        }
    }

    public void broadcastIncludingSelf(Packet packet) {
        this.broadcast(packet);
        if (this.tracker instanceof EntityPlayer) {
            ((EntityPlayer) this.tracker).playerConnection.sendPacket(packet);
        }
    }

    public void a() {
        Iterator iterator = this.trackedPlayers.iterator();

        while (iterator.hasNext()) {
            EntityPlayer entityplayer = (EntityPlayer) iterator.next();

            entityplayer.d(this.tracker);
        }
    }

    public void a(EntityPlayer entityplayer) {
        if (this.trackedPlayers.contains(entityplayer)) {
            entityplayer.d(this.tracker);
            this.trackedPlayers.remove(entityplayer);
        }
    }

    public void updatePlayer(EntityPlayer entityplayer) {
        org.spigotmc.AsyncCatcher.catchOp( "player tracker update"); // Spigot
        if (entityplayer != this.tracker) {
            double d0 = entityplayer.locX - (double) (this.xLoc / 32);
            double d1 = entityplayer.locZ - (double) (this.zLoc / 32);

            if (d0 >= (double) (-this.b) && d0 <= (double) this.b && d1 >= (double) (-this.b) && d1 <= (double) this.b) {
                if (!this.trackedPlayers.contains(entityplayer) && (this.d(entityplayer) || this.tracker.attachedToPlayer)) {
                    // CraftBukkit start - respect vanish API
                    if (this.tracker instanceof EntityPlayer) {
                        Player player = ((EntityPlayer) this.tracker).getBukkitEntity();
                        if (!entityplayer.getBukkitEntity().canSee(player)) {
                            return;
                        }
                    }

                    entityplayer.removeQueue.remove(Integer.valueOf(this.tracker.getId()));
                    // CraftBukkit end

                    this.trackedPlayerMap.put(entityplayer, true);
                    Packet packet = this.c();

                    // Spigot start - protocol patch
                    if ( tracker instanceof EntityPlayer) {
                        entityplayer.playerConnection.sendPacket(new PacketPlayOutPlayerInfo((EntityPlayer) tracker, PacketPlayOutPlayerInfo.PlayerInfo.ADD_PLAYER));
                        if (!entityplayer.getName().equals( entityplayer.listName ) && entityplayer.playerConnection.networkManager.getVersion() > 28) {
                            entityplayer.playerConnection.sendPacket(new PacketPlayOutPlayerInfo( (EntityPlayer) tracker, PacketPlayOutPlayerInfo.PlayerInfo.UPDATE_DISPLAY_NAME));
                        }
                    }
                    // Spigot end

                    if (packet != null) { // nPaper - don't use the method if packet equals null
                        entityplayer.playerConnection.sendPacket(packet);
                    }

                    if (!this.tracker.getDataWatcher().d()) {
                        entityplayer.playerConnection.sendPacket(new PacketPlayOutEntityMetadata(this.tracker.getId(), this.tracker.getDataWatcher(), true));
                    }

                    if (this.tracker instanceof EntityLiving) {
                        AttributeMapServer attributemapserver = (AttributeMapServer) ((EntityLiving) this.tracker).getAttributeMap();
                        Collection collection = attributemapserver.c();

                        // CraftBukkit start - If sending own attributes send scaled health instead of current maximum health
                        if (this.tracker.getId() == entityplayer.getId()) {
                            ((EntityPlayer) this.tracker).getBukkitEntity().injectScaledMaxHealth(collection, false);
                        }
                        // CraftBukkit end
                        if (!collection.isEmpty()) {
                            entityplayer.playerConnection.sendPacket(new PacketPlayOutUpdateAttributes(this.tracker.getId(), collection));
                        }
                    }

                    this.j = this.tracker.motX;
                    this.k = this.tracker.motY;
                    this.l = this.tracker.motZ;
                    if (this.u && !(packet instanceof PacketPlayOutSpawnEntityLiving)) {
                        entityplayer.playerConnection.sendPacket(new PacketPlayOutEntityVelocity(this.tracker.getId(), this.tracker.motX, this.tracker.motY, this.tracker.motZ));
                    }

                    if (this.tracker.vehicle != null) {
                        entityplayer.playerConnection.sendPacket(new PacketPlayOutAttachEntity(0, this.tracker, this.tracker.vehicle));
                    }

                    // CraftBukkit start
                    if (this.tracker.passenger != null) {
                        entityplayer.playerConnection.sendPacket(new PacketPlayOutAttachEntity(0, this.tracker.passenger, this.tracker));
                    }
                    // CraftBukkit end

                    if (this.tracker instanceof EntityInsentient && ((EntityInsentient) this.tracker).getLeashHolder() != null) {
                        entityplayer.playerConnection.sendPacket(new PacketPlayOutAttachEntity(1, this.tracker, ((EntityInsentient) this.tracker).getLeashHolder()));
                    }

                    if (this.tracker instanceof EntityLiving) {
                        for (int i = 0; i < 5; ++i) {
                            ItemStack itemstack = ((EntityLiving) this.tracker).getEquipment(i);

                            if (itemstack != null) {
                                entityplayer.playerConnection.sendPacket(new PacketPlayOutEntityEquipment(this.tracker.getId(), i, itemstack));
                            }
                        }
                    }

                    if (this.tracker instanceof EntityHuman) {
                        EntityHuman entityhuman = (EntityHuman) this.tracker;

                        if (entityhuman.isSleeping()) {
                            entityplayer.playerConnection.sendPacket(new PacketPlayOutBed(entityhuman, MathHelper.floor(this.tracker.locX), MathHelper.floor(this.tracker.locY), MathHelper.floor(this.tracker.locZ)));
                        }
                    }

                    // nPaper start - fix head packet spam
                    if(this.tracker instanceof EntityLiving) { // SportPaper - avoid processing entities that can't change head rotation
                         this.i = MathHelper.d(this.tracker.getHeadRotation() * 256.0F / 360.0F);
                         // SportPaper start
                         // This was originally introduced by CraftBukkit, though the implementation is wrong since it's broadcasting
                         // the packet again in a method that is already called for each player. This would create a very serious performance issue
                         // with high player and entity counts (each sendPacket call involves waking up the event loop and flushing the network stream).
                         // this.broadcast(new PacketPlayOutEntityHeadRotation(this.tracker, (byte) i));
                         entityplayer.playerConnection.sendPacket(new PacketPlayOutEntityHeadRotation(this.tracker, (byte) i));
                         // SportPaper end
                     }
                    // nPaper end

                    if (this.tracker instanceof EntityLiving) {
                        EntityLiving entityliving = (EntityLiving) this.tracker;
                        Iterator iterator = entityliving.getEffects().iterator();

                        while (iterator.hasNext()) {
                            MobEffect mobeffect = (MobEffect) iterator.next();

                            entityplayer.playerConnection.sendPacket(new PacketPlayOutEntityEffect(this.tracker.getId(), mobeffect));
                        }
                    }
                }
            } else if (this.trackedPlayers.contains(entityplayer)) {
                this.trackedPlayers.remove(entityplayer);
                entityplayer.d(this.tracker);
            }
        }
    }

    private boolean d(EntityPlayer entityplayer) {
        return entityplayer.r().getPlayerChunkMap().a(entityplayer, this.tracker.ah, this.tracker.aj);
    }

    public void scanPlayers(List list) {
        for (int i = 0; i < list.size(); ++i) {
            this.updatePlayer((EntityPlayer) list.get(i));
        }
    }

    private static Map<Class<? extends Entity>, Function<Entity, Packet>> packetCreators = new HashMap<>();
    static {
    	packetCreators.put(EntityItem.class, entity -> new PacketPlayOutSpawnEntity(entity, 2, 1));
        packetCreators.put(EntityPlayer.class, entity -> new PacketPlayOutNamedEntitySpawn((EntityHuman) entity));
        packetCreators.put(EntityMinecartAbstract.class, entity -> new PacketPlayOutSpawnEntity(entity, 10, ((EntityMinecartAbstract) entity).m()));
        packetCreators.put(EntityBoat.class, entity -> new PacketPlayOutSpawnEntity(entity, 1));
        packetCreators.put(EntityFishingHook.class, entity -> {
            final EntityHuman owner = ((EntityFishingHook) entity).owner;
            return new PacketPlayOutSpawnEntity(entity, 90, owner != null ? owner.getId() : entity.getId());
        });
        packetCreators.put(EntityArrow.class, entity -> {
        	final Entity shooter = ((EntityArrow) entity).shooter;
            return new PacketPlayOutSpawnEntity(entity, 60, shooter != null ? shooter.getId() : entity.getId());
        });
        packetCreators.put(EntitySnowball.class, entity -> new PacketPlayOutSpawnEntity(entity, 61));
        packetCreators.put(EntityPotion.class, entity -> new PacketPlayOutSpawnEntity(entity, 73, ((EntityPotion) entity).getPotionValue()));
        packetCreators.put(EntityThrownExpBottle.class, entity -> new PacketPlayOutSpawnEntity(entity, 75));
        packetCreators.put(EntityEnderPearl.class, entity -> new PacketPlayOutSpawnEntity(entity, 65));
        packetCreators.put(EntityEnderSignal.class, entity -> new PacketPlayOutSpawnEntity(entity, 72));
        packetCreators.put(EntityFireworks.class, entity -> new PacketPlayOutSpawnEntity(entity, 76));
        packetCreators.put(EntityEgg.class, entity -> new PacketPlayOutSpawnEntity(entity, 62));
        packetCreators.put(EntityTNTPrimed.class, entity -> new PacketPlayOutSpawnEntity(entity, 50));
        packetCreators.put(EntityEnderCrystal.class, entity -> new PacketPlayOutSpawnEntity(entity, 51));
        packetCreators.put(EntityFallingBlock.class, entity -> {
        	final EntityFallingBlock fallingBlock = (EntityFallingBlock) entity;
            return new PacketPlayOutSpawnEntity(entity, 70, Block.getId(fallingBlock.f()) | fallingBlock.data << 16);
        });
        packetCreators.put(EntityPainting.class, entity -> new PacketPlayOutSpawnEntityPainting((EntityPainting) entity));
        packetCreators.put(EntityItemFrame.class, entity -> {
        	final EntityItemFrame itemFrame = (EntityItemFrame) entity;
        	final PacketPlayOutSpawnEntity packet = new PacketPlayOutSpawnEntity(entity, 71, itemFrame.direction);
            packet.a(MathHelper.d((float) (itemFrame.x * 32)));
            packet.b(MathHelper.d((float) (itemFrame.y * 32)));
            packet.c(MathHelper.d((float) (itemFrame.z * 32)));
            return packet;
        });
        packetCreators.put(EntityLeash.class, entity -> {
        	final EntityLeash leash = (EntityLeash) entity;
        	final PacketPlayOutSpawnEntity packet = new PacketPlayOutSpawnEntity(entity, 77);
            packet.a(MathHelper.d((float) (leash.x * 32)));
            packet.b(MathHelper.d((float) (leash.y * 32)));
            packet.c(MathHelper.d((float) (leash.z * 32)));
            return packet;
        });
        packetCreators.put(EntityExperienceOrb.class, entity -> new PacketPlayOutSpawnEntityExperienceOrb((EntityExperienceOrb) entity));
    }
    
    private Packet createFireballPacket(EntityFireball entityfireball) {
        byte b0 = 63;
        if (entityfireball instanceof EntitySmallFireball) {
            b0 = 64;
        } else if (entityfireball instanceof EntityWitherSkull) {
            b0 = 66;
        }
        final PacketPlayOutSpawnEntity packet = new PacketPlayOutSpawnEntity(entityfireball, b0, entityfireball.shooter != null ? entityfireball.shooter.getId() : 0);
        packet.d((int) (entityfireball.dirX * 8000.0D));
        packet.e((int) (entityfireball.dirY * 8000.0D));
        packet.f((int) (entityfireball.dirZ * 8000.0D));
        return packet;
    }

    private Packet c() {
        if (this.tracker.dead) {
            // CraftBukkit start - Remove useless error spam, just return
            // p.warn("Fetching addPacket for removed entity");
            return null;
            // CraftBukkit end
        }

        if (this.tracker instanceof IAnimal || this.tracker instanceof EntityEnderDragon) {
            this.i = MathHelper.d(this.tracker.getHeadRotation() * 256.0F / 360.0F);
            return new PacketPlayOutSpawnEntityLiving((EntityLiving) this.tracker);
        }

        if (this.tracker instanceof EntityFireball) {
            return this.createFireballPacket((EntityFireball) this.tracker);
        }

        final Function<Entity, Packet> creator = packetCreators.get(this.tracker.getClass());
        if (creator != null) {
            return creator.apply(this.tracker);
        }

        throw new IllegalArgumentException("Don\'t know how to add " + this.tracker.getClass() + "!");
    }

    public void clear(EntityPlayer entityplayer) {
        org.spigotmc.AsyncCatcher.catchOp( "player tracker clear"); // Spigot
        if (this.trackedPlayers.contains(entityplayer)) {
            this.trackedPlayers.remove(entityplayer);
            entityplayer.d(this.tracker);
        }
    }

    private void sendPlayerPacket(EntityPlayer player, Packet packet) {
        player.playerConnection.sendPacket(packet);
    }
}
