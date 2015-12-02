package mw.editor;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.MathHelper;

public class TemplateBlockSelector {
	
	private static final float DEGtoRAD = (float) Math.PI / 180;
	
	public static int[] getSelector(EntityPlayer player) {
		
		float playerP = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch);
		float playerY = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw);
		float pitchC = -MathHelper.cos(-playerP * DEGtoRAD);

		BlockAreaModeClient bam = ModEditor.instance.bam;
		
		double[] pos = new double[]{
			player.prevPosX + (player.posX - player.prevPosX),
			player.prevPosY + (player.posY - player.prevPosY) + (double)(player.worldObj.isRemote ? player.getEyeHeight() - player.getDefaultEyeHeight() : player.getEyeHeight()),
			player.prevPosZ + (player.posZ - player.prevPosZ)
		};
		double[] vector = bam.coordsToTemplate( 
				pos[0] + (double)(MathHelper.sin(-playerY * DEGtoRAD - (float)Math.PI) * pitchC),
				pos[1] + (double) MathHelper.sin(-playerP * DEGtoRAD),
				pos[2] + (double)(MathHelper.cos(-playerY * DEGtoRAD - (float)Math.PI) * pitchC)
		);

		pos = bam.coordsToTemplate(pos[0], pos[1], pos[2]);
		vector[0] -= pos[0];
		vector[1] -= pos[1];
		vector[2] -= pos[2];

		double checkDistance = 5.0D;
		if (player instanceof EntityPlayerMP) {
			checkDistance = ((EntityPlayerMP)player).theItemInWorldManager.getBlockReachDistance();
		}
		
		double d = Double.POSITIVE_INFINITY;
		if (bam.sectionType == -1 || bam.sectionType == 0) {
			d = Math.min(d, intersectCuboid(pos, vector, 0, -1, -1, bam.templateWidth(), 0, 0));
		}
		if (bam.sectionType == -1 || bam.sectionType == 1) {
			d = Math.min(d, intersectCuboid(pos, vector, -1, 0, -1, 0, bam.templateHeight(), 0));
		}
		if (bam.sectionType == -1 || bam.sectionType == 3) {
			d = Math.min(d, intersectCuboid(pos, vector, -1, -1, 0, 0, 0, bam.templateDepth()));
		}

		if (d <= checkDistance) {
			double x = pos[0] + vector[0] * d;
			if (x > 0) {
				return new int[] { 0, (int)Math.floor(x) };
			}
			double y = pos[1] + vector[1] * d;
			if (y > 0) {
				return new int[] { 1, (int)Math.floor(y) };
			}
			double z = pos[2] + vector[2] * d;
			if (z > 0) {
				return new int[] { 2, (int)Math.floor(z) };
			}
		}
		
		return new int[] {-1, -1};
	}
	
	private static double intersectCuboid(double[] pos, double vector[], double x1, double y1, double z1, double x2, double y2, double z2) {
		
		double a = (x1 - pos[0]) / vector[0];
		double b = (x2 - pos[0]) / vector[0];
		
		double near = min(a, b);
		double far = max(a, b);
		
		a = (y1 - pos[1]) / vector[1];
		b = (y2 - pos[1]) / vector[1];
		
		near = max(near, min(a, b));
		far = min(far, max(a, b));
		
		a = (z1 - pos[2]) / vector[2];
		b = (z2 - pos[2]) / vector[2];
		
		near = max(near, min(a, b));
		far = min(far, max(a, b));
		
		if (near > far || far < 0) {
			return Double.POSITIVE_INFINITY;
		}
		
		return near;
	}
	
	private static double min(double a, double b) {
		if (a != a) {
			return b;
		}
		if (b != b) {
			return a;
		}
		return (a < b) ? a : b;
	}
	
	private static double max(double a, double b) {
		if (a != a) {
			return b;
		}
		if (b != b) {
			return a;
		}
		return (a > b) ? a : b;
	}
}
