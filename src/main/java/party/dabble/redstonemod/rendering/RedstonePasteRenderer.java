package party.dabble.redstonemod.rendering;

import java.util.EnumMap;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import party.dabble.redstonemod.block.BlockRedstonePasteWire;
import party.dabble.redstonemod.util.EnumModel;
import party.dabble.redstonemod.util.ModelLookup;
import party.dabble.redstonemod.util.PowerLookup;

@SideOnly(Side.CLIENT)
public class RedstonePasteRenderer extends TileEntitySpecialRenderer {

	@Override
	public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float foo, int wat) {
		World world = tileEntity.getWorld();
		BlockPos pos = tileEntity.getPos();
		Block block = world.getBlockState(pos).getBlock();

		// Have to do this because world.getBlockState(pos) occasionally returns the blockstate of a nearby air block for some reason
		if (!(block instanceof BlockRedstonePasteWire))
			return;

		int colour = (!BlockRedstonePasteWire.isDebugWorld) ? calculateColour(PowerLookup.getPower(pos, world))
				: calculateColour(((Integer)world.getBlockState(pos).getValue(BlockRedstonePasteWire.POWER)).byteValue());
		int red = colour >> 16 & 255;
		int green = colour >> 8 & 255;
		int blue = colour & 255;
		this.bindTexture(new ResourceLocation("redstonemod:textures/blocks/redstone_paste.png"));

		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);

		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldRenderer = tessellator.getWorldRenderer();
		worldRenderer.startDrawingQuads();
		worldRenderer.setColorRGBA(red, green, blue, 255);
		worldRenderer.setNormal(0, 1, 0);

		EnumMap<EnumFacing, EnumModel> model = ModelLookup.getModel(pos, world);
		for (Entry<EnumFacing, EnumModel> face : model.entrySet())
			this.drawFace(worldRenderer, face.getKey(), face.getValue());

		tessellator.draw();

		GlStateManager.popMatrix();
	}

	public static int calculateColour(byte powerLevel) {
		float powerPercentage = powerLevel / 15f;
		int red;

		if (powerLevel == 0)
			red = (int)(0.3 * 255);
		else
			red = (int)((powerPercentage * 0.6 + 0.4) * 255);

		int green = Math.max(0, (int)((powerPercentage * powerPercentage * 0.7 - 0.5) * 255));
		int blue = Math.max(0, (int)((powerPercentage * powerPercentage * 0.6 - 0.7) * 255));
		return 0xFF000000 | applyMoisture(red) << 16 | green << 8 | blue;
	}

	private final static int AMOUNT_OF_LIGHT_ABSORBED = 36;
	private final static int MIN_LIGHT = 76;
	private final static int MAX_LIGHT = 255;

	private static int applyMoisture(int colour) {
		return colour - AMOUNT_OF_LIGHT_ABSORBED * (1 - (colour - MIN_LIGHT) / (MAX_LIGHT - MIN_LIGHT));
	}

	private void drawFace(WorldRenderer worldRenderer, EnumFacing face, EnumModel model) {
		double minU = model.getMinU();
		double maxU = model.getMaxU();
		double minV = model.getMinV();
		double maxV = model.getMaxV();

		switch (face) {
			case DOWN:
				worldRenderer.addVertexWithUV(0, 0.25 / 16, 0, minU, minV);
				worldRenderer.addVertexWithUV(0, 0.25 / 16, 1, minU, maxV);
				worldRenderer.addVertexWithUV(1, 0.25 / 16, 1, maxU, maxV);
				worldRenderer.addVertexWithUV(1, 0.25 / 16, 0, maxU, minV);
				break;

			case UP:
				worldRenderer.addVertexWithUV(0, 15.75 / 16, 0, minU, maxV);
				worldRenderer.addVertexWithUV(1, 15.75 / 16, 0, maxU, maxV);
				worldRenderer.addVertexWithUV(1, 15.75 / 16, 1, maxU, minV);
				worldRenderer.addVertexWithUV(0, 15.75 / 16, 1, minU, minV);
				break;

			case NORTH:
				worldRenderer.addVertexWithUV(0, 0, 0.25 / 16, minU, maxV);
				worldRenderer.addVertexWithUV(1, 0, 0.25 / 16, maxU, maxV);
				worldRenderer.addVertexWithUV(1, 1, 0.25 / 16, maxU, minV);
				worldRenderer.addVertexWithUV(0, 1, 0.25 / 16, minU, minV);
				break;

			case SOUTH:
				worldRenderer.addVertexWithUV(0, 0, 15.75 / 16, maxU, maxV);
				worldRenderer.addVertexWithUV(0, 1, 15.75 / 16, maxU, minV);
				worldRenderer.addVertexWithUV(1, 1, 15.75 / 16, minU, minV);
				worldRenderer.addVertexWithUV(1, 0, 15.75 / 16, minU, maxV);
				break;

			case WEST:
				worldRenderer.addVertexWithUV(0.25 / 16, 0, 0, maxU, maxV);
				worldRenderer.addVertexWithUV(0.25 / 16, 1, 0, maxU, minV);
				worldRenderer.addVertexWithUV(0.25 / 16, 1, 1, minU, minV);
				worldRenderer.addVertexWithUV(0.25 / 16, 0, 1, minU, maxV);
				break;

			case EAST:
				worldRenderer.addVertexWithUV(15.75 / 16, 0, 0, minU, maxV);
				worldRenderer.addVertexWithUV(15.75 / 16, 0, 1, maxU, maxV);
				worldRenderer.addVertexWithUV(15.75 / 16, 1, 1, maxU, minV);
				worldRenderer.addVertexWithUV(15.75 / 16, 1, 0, minU, minV);
				break;
		}
	}
}
