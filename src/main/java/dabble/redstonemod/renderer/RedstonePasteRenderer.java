package dabble.redstonemod.renderer;

import java.util.EnumMap;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import dabble.redstonemod.block.BlockRedstonePasteWire;
import dabble.redstonemod.util.EnumModel;

@SideOnly(Side.CLIENT)
public class RedstonePasteRenderer extends TileEntitySpecialRenderer {

	@Override
	public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float foo, int wat) {
		World worldIn = tileEntity.getWorld();
		BlockPos pos = tileEntity.getPos();
		IBlockState state = worldIn.getBlockState(pos);
		Block block = state.getBlock();

		// Have to do this because worldIn.getBlockState(pos) occasionally returns the blockstate of a nearby air block for some reason
		if (!(block instanceof BlockRedstonePasteWire))
			return;

		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldRenderer = tessellator.getWorldRenderer();
		EnumMap<EnumFacing, EnumModel> model = ((BlockRedstonePasteWire) block).getModel(worldIn, pos);
		this.bindTexture(new ResourceLocation("redstonemod:textures/blocks/redstone_paste.png"));

		int colour = colorMultiplier(((Integer) state.getValue(BlockRedstonePasteWire.POWER)).intValue());
		int red = colour >> 16 & 255;
		int green = colour >> 8 & 255;
		int blue = colour & 255;

		GlStateManager.pushMatrix();
		GlStateManager.pushAttrib();
		GlStateManager.translate(x, y, z);

		worldRenderer.startDrawingQuads();
		worldRenderer.setColorRGBA(red, green, blue, 255);

		for (Entry<EnumFacing, EnumModel> face : model.entrySet())
			drawFace(worldRenderer, face);

		tessellator.draw();

		GlStateManager.popAttrib();
		GlStateManager.popMatrix();
	}

	private int colorMultiplier(int powerLevel) {
		float f = (float) powerLevel / 15.0F;
		float f1 = f * 0.6F + 0.4F;

		if (powerLevel == 0) {
			f1 = 0.3F;
		}

		float f2 = f * f * 0.7F - 0.5F;
		float f3 = f * f * 0.6F - 0.7F;

		if (f2 < 0.0F) {
			f2 = 0.0F;
		}

		if (f3 < 0.0F) {
			f3 = 0.0F;
		}

		int j = MathHelper.clamp_int((int) (f1 * 255.0F), 0, 255);
		int k = MathHelper.clamp_int((int) (f2 * 255.0F), 0, 255);
		int l = MathHelper.clamp_int((int) (f3 * 255.0F), 0, 255);
		return -16777216 | j << 16 | k << 8 | l;
	}

	private void drawFace(WorldRenderer worldRenderer, Entry<EnumFacing, EnumModel> face) {
		EnumModel model = face.getValue();
		double minU = model.getMinU();
		double maxU = model.getMaxU();
		double minV = model.getMinV();
		double maxV = model.getMaxV();

		switch (face.getKey()) {
			case DOWN:
				worldRenderer.setNormal(0, 1, 0);
				worldRenderer.addVertexWithUV(0, 0.25 / 16, 0, minU, minV);
				worldRenderer.addVertexWithUV(0, 0.25 / 16, 1, minU, maxV);
				worldRenderer.addVertexWithUV(1, 0.25 / 16, 1, maxU, maxV);
				worldRenderer.addVertexWithUV(1, 0.25 / 16, 0, maxU, minV);
				break;
			case UP:
				worldRenderer.setNormal(0, -1, 0);
				worldRenderer.addVertexWithUV(0, 1 - 0.25 / 16, 0, maxU, minV);
				worldRenderer.addVertexWithUV(1, 1 - 0.25 / 16, 0, minU, minV);
				worldRenderer.addVertexWithUV(1, 1 - 0.25 / 16, 1, minU, maxV);
				worldRenderer.addVertexWithUV(0, 1 - 0.25 / 16, 1, maxU, maxV);
				break;
			case NORTH:
				worldRenderer.setNormal(0, 0, 1);
				worldRenderer.addVertexWithUV(0, 0, 0.25 / 16, minU, maxV);
				worldRenderer.addVertexWithUV(1, 0, 0.25 / 16, maxU, maxV);
				worldRenderer.addVertexWithUV(1, 1, 0.25 / 16, maxU, minV);
				worldRenderer.addVertexWithUV(0, 1, 0.25 / 16, minU, minV);
				break;
			case SOUTH:
				worldRenderer.setNormal(0, 0, -1);
				worldRenderer.addVertexWithUV(0, 0, 1 - 0.25 / 16, maxU, maxV);
				worldRenderer.addVertexWithUV(0, 1, 1 - 0.25 / 16, maxU, minV);
				worldRenderer.addVertexWithUV(1, 1, 1 - 0.25 / 16, minU, minV);
				worldRenderer.addVertexWithUV(1, 0, 1 - 0.25 / 16, minU, maxV);
				break;
			case WEST:
				worldRenderer.setNormal(1, 0, 0);
				worldRenderer.addVertexWithUV(0.25 / 16, 0, 0, maxU, maxV);
				worldRenderer.addVertexWithUV(0.25 / 16, 1, 0, maxU, minV);
				worldRenderer.addVertexWithUV(0.25 / 16, 1, 1, minU, minV);
				worldRenderer.addVertexWithUV(0.25 / 16, 0, 1, minU, maxV);
				break;
			case EAST:
				worldRenderer.setNormal(-1, 0, 0);
				worldRenderer.addVertexWithUV(1 - 0.25 / 16, 0, 0, minU, maxV);
				worldRenderer.addVertexWithUV(1 - 0.25 / 16, 0, 1, maxU, maxV);
				worldRenderer.addVertexWithUV(1 - 0.25 / 16, 1, 1, maxU, minV);
				worldRenderer.addVertexWithUV(1 - 0.25 / 16, 1, 0, minU, minV);
				break;
		}
	}
}