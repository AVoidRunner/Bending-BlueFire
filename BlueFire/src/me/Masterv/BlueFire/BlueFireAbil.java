package me.Masterv.BlueFire;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.FireAbility;
import com.projectkorra.projectkorra.configuration.ConfigManager;
import com.projectkorra.projectkorra.util.DamageHandler;
import com.projectkorra.projectkorra.util.ParticleEffect;

/*
 * extends FireAbility
 * This defines what element the addon will go under. There's also ChiAbility and AvatarAbility
 * aside from each of the 4 elements and their subelements.
 * 
 * implements AddonAbility
 * This is telling projectkorra that the this is an AddonAbility as opposed to
 * ComboAbility, PassiveAbility and SubAbility.
 * Notice: You should always implement AddonAbility, unless it's not an ability of course.
 * Example: For a ComboAbility you'd use "ComboAbility, AddonAbility"
 */
public class BlueFireAbil extends FireAbility implements AddonAbility {

	/*
	 * Variables you create can go here for organization.
	 * If you have your own way for organiztion, then use that.
	 */
	private Location location;
	private Location origin;
	private Vector direction;
	
	private double speedy = ConfigManager.getConfig().getDouble("ExtraAbilities.Mastervrunner.Fire.BlueFire.Speed");
	private long coolingDown = ConfigManager.getConfig().getLong("ExtraAbilities.Mastervrunner.Fire.BlueFire.Cooldown");
	private double rangee = ConfigManager.getConfig().getDouble("ExtraAbilities.Mastervrunner.Fire.BlueFire.Range");

	/*
	 * The constructor used to determine who the player is and to start the ability.
	 */
	public BlueFireAbil(Player player) {
		super(player);
		
		/*
		 * Doesn't allow the ability to progress if it's on cooldown.
		 */
		if (bPlayer.isOnCooldown(this)) {
			return;
		}
		
		/*
		 * Custom method that we will define later.
		 */
		setFields();
		
		/*
		 * Starts the ability.
		 */
		start();
		
		/*
		 * Puts the ability on cooldown as soon as it starts.
		 */
		bPlayer.addCooldown(this);
	}

	/*
	 * Place to define variables at the start of an ability.
	 * Notice the "setFields()" included here and in the constructor.
	 * You create variables above the constructor and here is where you define them.
	 */
	private void setFields() {
		/*
		 * We want to get a location that represents the start of the ability and use it for later.
		 */
		this.origin = player.getLocation().clone().add(0, 1, 0);
		
		/*
		 * Then we use another location variable so that we can tell the ability what to do.
		 */
		this.location = origin.clone();
		
		/*
		 * Since this is a "blast" ability we're going to get the players direction
		 * so that we tell the ability which direction to go in.
		 */
		this.direction = player.getLocation().getDirection();
	}

	/*
	 * Method that controls what the abilities does.
	 */
	@Override
	public void progress() {
		/*
		 * Makes sure the ability doesn't progress when there's no player. You could also make sure
		 * they don't switch worlds.
		 * English: If the player is dead or the player in not online, stop.
		 */
		if (player.isDead() || !player.isOnline()) {
			remove();
			return;
		}
		
		/*
		 * If the ability progresses beyond 20 blocks it will stop and be put on cooldown.
		 * English: If our "origin" variable is more than 20 blocks from our "location" variable, stop.
		 */
		
		double speed = speedy;
		
		for(int i = 1; i <= speed; i++) {
		
			if (origin.distance(location) > rangee) {
				remove();
				return;
			}
			
			
			
			/*
			 * Updates the location every tick to go in the direction the player is looking, with a speed of 1.
			 */
			location.add(direction.multiply(1));
			
			/*
			 * Defines the particle effect that displays at every location point.
			 * Depending on your IDE, you should be able to hover over "display" to see
			 * what each of the variabels in the paranthesis mean.
			 */
			
			int count = 50;
			
			player.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, location.getX(), location.getY(), location.getZ(), count, 0.25, 0.1, 0.25, 0.01);
			
			/*
			 * Stops the ability if it hits a block.
			 * English: If the location of the ability is equal to that of a block, stop.
			 */
			if (GeneralMethods.isSolid(location.getBlock())) {
				remove();
				return;
			}
			
			/*
			 * Loop that checks for entities wherever our "location" variable is.
			 * English: If there is ever an entity around the variable "location" call it "entity"
			 */
			for (Entity entity : GeneralMethods.getEntitiesAroundPoint(location, 1)) {
				/*
				 * The effects we apply to the "entity"
				 * English: If there is an entity which is living and is not equal to the player using the ability, 
				 * the entity will take damage and the ability will stop progressing.
				 */
				if ((entity instanceof LivingEntity) && entity.getUniqueId() != player.getUniqueId()) {
					DamageHandler.damageEntity(entity, 2, this);
					
					// Get velocity unit vector:
					Vector unitVector = entity.getLocation().toVector().subtract(location.toVector()).normalize();
					// Set speed and push entity:
					entity.setVelocity(unitVector.multiply(speed));
					entity.setVelocity(entity.getVelocity().add(new Vector(0,speed,0)));
					
					remove();
					return;
				}
			}
		}
	}

	/*
	 * The duration of the cooldown. This is useful to some aspects of the ProjectKorra API (like bending previews)
	 * and for other addon developers to use. Set this to return a 'long' variable representing your cooldown.
	 */
	@Override
	public long getCooldown() {
		//return 1000;
		return coolingDown;
	}

	/*
	 * The location of the ability. This is useful for some aspects of the ProjectKorra API (like ability collisions)
	 * and for other addon developers to use. Because we are not setting up any collisions and I'm not worried about
	 * addon developers using this, I'm setting it to null. Otherwise, set it to return the location of your ability.
	 */
	@Override
	public Location getLocation() {
		return null;
	}

	/*
	 * The name of the ability.
	 * This will appear when using the /bending display commands, /bending who commands, and in a BendingBoard plugin
	 * you may or may not have.
	 */
	@Override
	public String getName() {
		return "BlueFire";
	}
	
	/*
	 * The description for the ability.
	 * Displays in /b h [abilityname]
	 */
	@Override
	public String getDescription() {
		return "A fire, but blue";
	}
	
	/*
	 * The instruction for the ability.
	 * Displays in /b h [abilityname]
	 */
	@Override
	public String getInstructions() {
		return "Click";
	}

	/*
	 * The author of the ability.
	 * Displays in /b h [abilityname] in more recent versions of ProjectKorra.
	 * Also useful for putting credit in the getDescription() method or the load() method if you so choose.
	 */
	@Override
	public String getAuthor() {
		return "MasterV";
	}

	/*
	 * The version of the ability.
	 * Displays in /b h [abilityname] in more recent versions of ProjectKorra.
	 */
	@Override
	public String getVersion() {
		return "1.0";
	}

	/*
	 * Does this ability harm things?
	 * This is not necessary unless you need to be concerned with whether or not this ability will work in regions.
	 */
	@Override
	public boolean isHarmlessAbility() {
		return false;
	}

	/*
	 * Do you need to sneak for the ability (shift)?
	 * This is not necessary.
	 */
	@Override
	public boolean isSneakAbility() {
		return false;
	}

	/*
	 * This method is run whenever the ability is loaded into a server.
	 * Restart/reload
	 */
	@Override
	public void load() {
		/*
		 * Grabs information from the AbilityListener class so it knows when to start.
		 */
		ProjectKorra.plugin.getServer().getPluginManager().registerEvents(new BlueFireListener(), ProjectKorra.plugin);
		
		
		ConfigManager.getConfig().addDefault("ExtraAbilities.Mastervrunner.Fire.BlueFire.Cooldown", 1000);
		ConfigManager.getConfig().addDefault("ExtraAbilities.Mastervrunner.Fire.BlueFire.Range", 50);
		ConfigManager.getConfig().addDefault("ExtraAbilities.Mastervrunner.Fire.BlueFire.Speed", 5);
		
		
		/*
		 * Log message that appears when the ability is loaded.
		 */
		ProjectKorra.log.info("Successfully enabled " + getName() + " by " + getAuthor());
	}

	/*
	 * This method is run whenever the ability is disabled from a server.
	 * Restart/reload
	 */
	@Override
	public void stop() {
		/*
		 * Log message that appears when the ability is disabled.
		 */
		ProjectKorra.log.info("Successfully disabled " + getName() + " by " + getAuthor());
		
		/*
		 * When the server stops or reloads, the ability will stop what it's doing and remove.
		 */
		super.remove();
	}

}