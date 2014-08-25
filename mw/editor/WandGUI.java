package mw.editor;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.EventPriority;
import net.minecraftforge.event.ForgeSubscribe;

import org.lwjgl.opengl.GL11;

public class WandGUI {

	private static final Minecraft mc = Minecraft.getMinecraft();

	@ForgeSubscribe(priority = EventPriority.NORMAL)
	public void onRenderWorld(RenderWorldLastEvent event) {
		if (this.mc.thePlayer != null && this.mc.thePlayer.capabilities.isCreativeMode) {
			ItemStack is = this.mc.thePlayer.inventory.getCurrentItem();
			if (is != null && is.itemID == ModEditor.instance.wandId + 256) {
				BlockAreaMode bam = ModEditor.instance.bam;
				int[] area = ModEditor.instance.bam.area;
				double posX = this.mc.thePlayer.prevPosX + (this.mc.thePlayer.posX - this.mc.thePlayer.prevPosX) * event.partialTicks;
				double posY = this.mc.thePlayer.prevPosY + (this.mc.thePlayer.posY - this.mc.thePlayer.prevPosY) * event.partialTicks;
				double posZ = this.mc.thePlayer.prevPosZ + (this.mc.thePlayer.posZ - this.mc.thePlayer.prevPosZ) * event.partialTicks;
				
				RenderHelper.disableStandardItemLighting();
				
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glEnable(GL11.GL_ALPHA_TEST);
				GL11.glAlphaFunc(GL11.GL_GREATER, 0.0F);
				GL11.glDisable(GL11.GL_TEXTURE_2D);
				GL11.glDepthMask(false);
				GL11.glPushMatrix();
				
				
				GL11.glTranslated(-posX, -posY, -posZ);
				GL11.glColor3f(1, 1, 1);
				GL11.glLineWidth(3);
				
				MovingObjectPosition mop = ModEditor.instance.wand.getMovingObjectPositionFromPlayer(this.mc.theWorld, this.mc.thePlayer, true);
				int x1;
                int y1;
                int z1;
				if (mop != null && mop.typeOfHit == EnumMovingObjectType.TILE) {
					x1 = mop.blockX;
	                y1 = mop.blockY;
	                z1 = mop.blockZ;
				} else {
					x1 = (int) Math.floor(this.mc.thePlayer.posX);
			        y1 = (int) Math.floor(this.mc.thePlayer.posY + (this.mc.theWorld.isRemote ? this.mc.thePlayer.getEyeHeight() - this.mc.thePlayer.getDefaultEyeHeight() : this.mc.thePlayer.getEyeHeight()));
			        z1 = (int) Math.floor(this.mc.thePlayer.posZ);
					if (y1 > 255) {
						y1 = 255;
					} else if (y1 < 0) {
						y1 = 0;
					}
				}
				
				if (bam.mode == 0 || bam.mode == 1 || bam.mode == 2 || bam.mode == 3 || bam.mode == 5) {
					GL11.glDepthFunc(GL11.GL_GREATER);
					this.renderBox(x1, y1, z1, x1 + 1, y1 + 1, z1 + 1, 255, 255, 255, 63);
					GL11.glDepthFunc(GL11.GL_LEQUAL);
					this.renderBox(x1, y1, z1, x1 + 1, y1 + 1, z1 + 1, 255, 255, 255, 127);
				}
				if (bam.startSet || bam.endSet) {
					
					if (bam.startSet && bam.endSet) {
						int minX = min(area[0], area[3]);
						int maxX = max(area[0], area[3]);
						int minY = min(area[1], area[4]);
						int maxY = max(area[1], area[4]);
						int minZ = min(area[2], area[5]);
						int maxZ = max(area[2], area[5]);
						GL11.glDepthFunc(GL11.GL_GREATER);
						this.renderBox(minX, minY, minZ, maxX + 1, maxY + 1, maxZ + 1, 0, 255, 0, 63);
						GL11.glDepthFunc(GL11.GL_LEQUAL);
						this.renderBox(minX, minY, minZ, maxX + 1, maxY + 1, maxZ + 1, 0, 255, 0, 127);
						
						if (bam.mode == 6) {
			                int x2 = x1 + area[3] - area[0];
			                int y2 = y1 + area[4] - area[1];
			                int z2 = z1 + area[5] - area[2];
			                
			                minX = min(x1, x2);
							maxX = max(x1, x2);
							minY = min(y1, y2);
							maxY = max(y1, y2);
							minZ = min(z1, z2);
							maxZ = max(z1, z2);
							
							GL11.glDepthFunc(GL11.GL_GREATER);
							this.renderBox(minX, minY, minZ, maxX + 1, maxY + 1, maxZ + 1, 127, 127, 127, 63);
							GL11.glDepthFunc(GL11.GL_LEQUAL);
							this.renderBox(minX, minY, minZ, maxX + 1, maxY + 1, maxZ + 1, 127, 127, 127, 127);
							
							GL11.glDepthFunc(GL11.GL_GREATER);
							this.renderBox(x1, y1, z1, x1 + 1, y1 + 1, z1 + 1, 255, 255, 255, 63);
							GL11.glDepthFunc(GL11.GL_LEQUAL);
							this.renderBox(x1, y1, z1, x1 + 1, y1 + 1, z1 + 1, 255, 255, 255, 127);
	
							GL11.glDepthFunc(GL11.GL_GREATER);
							this.renderBox(x2, y2, z2, x2 + 1, y2 + 1, z2 + 1, 0, 0, 0, 63);
							GL11.glDepthFunc(GL11.GL_LEQUAL);
							this.renderBox(x2, y2, z2, x2 + 1, y2 + 1, z2 + 1, 0, 0, 0, 63);
						}
					}
					
					if (bam.startSet) {
						GL11.glDepthFunc(GL11.GL_GREATER);
						this.renderBox(area[0], area[1], area[2], area[0] + 1, area[1] + 1, area[2] + 1, 255, 0, 0, 63);
						GL11.glDepthFunc(GL11.GL_LEQUAL);
						this.renderBox(area[0], area[1], area[2], area[0] + 1, area[1] + 1, area[2] + 1, 255, 0, 0, 127);
					}
					if (bam.endSet) {
						GL11.glDepthFunc(GL11.GL_GREATER);
						this.renderBox(area[3], area[4], area[5], area[3] + 1, area[4] + 1, area[5] + 1, 0, 0, 255, 63);
						GL11.glDepthFunc(GL11.GL_LEQUAL);
						this.renderBox(area[3], area[4], area[5], area[3] + 1, area[4] + 1, area[5] + 1, 0, 0, 255, 127);
					}
					
				}
				GL11.glDepthFunc(GL11.GL_LEQUAL);
				GL11.glPopMatrix();

				GL11.glDepthMask(true);
				GL11.glEnable(GL11.GL_TEXTURE_2D);
				GL11.glDisable(GL11.GL_BLEND);
				GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
				
				RenderHelper.enableStandardItemLighting();
			}
		}
	}
	
	@ForgeSubscribe(priority = EventPriority.NORMAL)
	public void onRenderHUD(RenderGameOverlayEvent event) {
		if (!event.isCancelable() && event.type == ElementType.EXPERIENCE && this.mc.thePlayer != null && this.mc.thePlayer.capabilities.isCreativeMode) {
			ItemStack is = this.mc.thePlayer.inventory.getCurrentItem();
			BlockAreaMode bam = ModEditor.instance.bam;
			if (is != null && is.itemID == ModEditor.instance.wandId + 256 && bam.mode >= 0) {
				FontRenderer fr = RenderManager.instance.getFontRenderer();
				fr.drawStringWithShadow(I18n.getString("mw.editor.Mode" + ((Integer)bam.mode).toString()), 2, 2, 0xffffff);
				String blockName;
				if (bam.block.blockId == 0) {
					blockName = I18n.getString("mw.editor.blockAir");
				} else if (Item.itemsList[bam.block.blockId] != null){
					blockName = I18n.getString(Item.itemsList[bam.block.blockId].getUnlocalizedName(new ItemStack(bam.block.blockId, 1, bam.block.metadata)) + ".name");
				} else {
					blockName = Block.blocksList[bam.block.blockId].getLocalizedName();
				}
				fr.drawStringWithShadow(I18n.getString("mw.editor.selectedBlock") + ": " + blockName + " (" + new Integer(bam.block.blockId).toString() + ") - " + new Integer(bam.block.metadata).toString(), 2, 10, 0xffffff);
			}
		}
	}
	
	private void renderBox(int x1, int y1, int z1, int x2, int y2, int z2, int r, int g, int b, int a) {
		Tessellator t = Tessellator.instance;
		t.startDrawing(GL11.GL_LINE_LOOP);
		
		t.setColorRGBA(r, g, b, a);
		t.addVertex(x1, y1, z1);
		t.addVertex(x1, y1, z2);
		t.addVertex(x2, y1, z2);
		t.addVertex(x2, y1, z1);
		
		t.draw();
		
		t.startDrawing(GL11.GL_LINE_LOOP);
		t.setColorRGBA(r, g, b, a);
		
		GL11.glColor4f(r, g, b, a);
		t.addVertex(x1, y2, z1);
		t.addVertex(x1, y2, z2);
		t.addVertex(x2, y2, z2);
		t.addVertex(x2, y2, z1);
		
		t.draw();
		
		t.startDrawing(GL11.GL_LINES);
		t.setColorRGBA(r, g, b, a);
		
		t.addVertex(x1, y1, z1);
		t.addVertex(x1, y2, z1);

		t.addVertex(x2, y1, z1);
		t.addVertex(x2, y2, z1);

		t.addVertex(x2, y1, z2);
		t.addVertex(x2, y2, z2);

		t.addVertex(x1, y1, z2);
		t.addVertex(x1, y2, z2);
		
		t.draw();
	}
	
	private static int min(int a, int b) {
		if (a < b) {
			return a;
		}
		return b;
	}
	
	private static int max(int a, int b) {
		if (a < b) {
			return b;
		}
		return a;
	}
}
