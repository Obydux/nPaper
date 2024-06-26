package net.minecraft.server;

// CraftBukkit start
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.github.paperspigot.PaperSpigotConfig;
// CraftBukkit end

public class EntityEnderPearl extends EntityProjectile {

    private Location lastValidLocation; // nPaper - antipearl glitch

    public EntityEnderPearl(World world) {
        super(world);
         this.loadChunks = world.paperSpigotConfig.loadUnloadedEnderPearls; // PaperSpigot
    }

    public EntityEnderPearl(World world, EntityLiving entityliving) {
        super(world, entityliving);
        this.loadChunks = world.paperSpigotConfig.loadUnloadedEnderPearls; // PaperSpigot
        if (PaperSpigotConfig.fixEnderPearlGlitch) this.lastValidLocation = entityliving.getBukkitEntity().getLocation(); // nPaper - antipearl glitch - fix nullpointer
    }

    // nPaper start - antipearl glitch
    public void h() {
    	final EntityLiving entityliving = this.getShooter();
        if (entityliving != null && entityliving instanceof EntityHuman && !entityliving.isAlive()) {
            this.die();
            return;
        }
        if (PaperSpigotConfig.fixEnderPearlGlitch && this.world.getCubes(this, this.boundingBox.grow(0.225D, 0.1D, 0.225D)).isEmpty()) {
            this.lastValidLocation = getBukkitEntity().getLocation();
        }
        super.h();
    }
    // nPaper end


    protected void a(MovingObjectPosition movingobjectposition) {
        if (movingobjectposition.entity != null) {
            movingobjectposition.entity.damageEntity(DamageSource.projectile(this, this.getShooter()), 0.0F);
        }

        // PaperSpigot start - Remove entities in unloaded chunks
        if (inUnloadedChunk && world.paperSpigotConfig.removeUnloadedEnderPearls) {
            die();
        }
        // PaperSpigot end

        for (int i = 0; i < 32; ++i) {
            this.world.addParticle("portal", this.locX, this.locY + this.random.nextDouble() * 2.0D, this.locZ, this.random.nextGaussian(), 0.0D, this.random.nextGaussian());
        }

        if (!this.world.isStatic) {
            if (this.getShooter() != null && this.getShooter() instanceof EntityPlayer) {
                EntityPlayer entityplayer = (EntityPlayer) this.getShooter();

                if (entityplayer.playerConnection.b().isConnected() && entityplayer.world == this.world) {
                    // CraftBukkit start - Fire PlayerTeleportEvent
                    CraftPlayer player = entityplayer.getBukkitEntity();

                    Location location = PaperSpigotConfig.fixEnderPearlGlitch ? (movingobjectposition.entity == null ? this.lastValidLocation.clone() : getBukkitEntity().getLocation()) : getBukkitEntity().getLocation(); // snHose - antipearl glitch
                    location.setPitch(player.getLocation().getPitch());
                    location.setYaw(player.getLocation().getYaw());

                    PlayerTeleportEvent teleEvent = new PlayerTeleportEvent(player, player.getLocation(), location, PlayerTeleportEvent.TeleportCause.ENDER_PEARL);
                    Bukkit.getPluginManager().callEvent(teleEvent);

                    if (!teleEvent.isCancelled() && !entityplayer.playerConnection.isDisconnected()) {
                        if (this.getShooter().am()) {
                            this.getShooter().mount((Entity) null);
                        }

                        entityplayer.playerConnection.teleport(teleEvent.getTo());
                        this.getShooter().fallDistance = 0.0F;
                        CraftEventFactory.entityDamage = this;
                        this.getShooter().damageEntity(DamageSource.FALL, 5.0F);
                        CraftEventFactory.entityDamage = null;
                    }
                    // CraftBukkit end
                }
            }

            this.die();
        }
    }
}
