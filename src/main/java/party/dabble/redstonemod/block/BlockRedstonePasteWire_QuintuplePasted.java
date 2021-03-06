package party.dabble.redstonemod.block;

import java.util.EnumSet;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.World;
import party.dabble.redstonemod.init.ModBlocks;

public class BlockRedstonePasteWire_QuintuplePasted extends BlockRedstonePasteWire {
	public static final PropertyEnum PASTEDSIDES = PropertyEnum.create("pasted_sides", EnumPasting.class);

	public BlockRedstonePasteWire_QuintuplePasted(String unlocalisedName) {
		super(unlocalisedName, (byte)5);
		this.setDefaultState(this.blockState.getBaseState()
				.withProperty(PASTEDSIDES, EnumPasting.DUNSW)
				.withProperty(POWER, Integer.valueOf(0)));
	}

	@Override
	public EnumFacing[] getPastedSides(IBlockState state) {
		return ((EnumPasting)state.getValue(PASTEDSIDES)).sides;
	}

	@Override
	public EnumSet<EnumFacing> getPastedSidesSet(IBlockState state) {
		return EnumSet.copyOf(((EnumPasting)state.getValue(PASTEDSIDES)).sideSet);
	}

	@Override
	public boolean isPastedOnSide(EnumFacing side, IBlockState state) {
		return ((EnumPasting)state.getValue(PASTEDSIDES)).sideSet.contains(side);
	}

	@Override
	public IBlockState pasteAdditionalSide(EnumFacing side, IBlockState state, BlockPos pos, EntityPlayer player, World world) {
		return null;
	}

	static IBlockState getStateFromSides(EnumSet<EnumFacing> sides) {
		StringBuilder pasting = new StringBuilder(5);

		for (EnumFacing side : sides)
			pasting.append(side.getName().charAt(0));

		return ModBlocks.redstone_paste_quintuple_pasted.getDefaultState().withProperty(PASTEDSIDES, EnumPasting.valueOf(pasting.toString().toUpperCase()));
	}

	@Override
	protected EnumSet<EnumFacing> getValidPastedSides(IBlockState state, BlockPos pos, World world) {
		EnumSet<EnumFacing> validPastedSides = EnumSet.noneOf(EnumFacing.class);

		for (EnumFacing pastedSide : ((EnumPasting)state.getValue(PASTEDSIDES)).sides) {

			if (canPasteOnSideOfBlock(pastedSide.getOpposite(), pos.offset(pastedSide), world))
				validPastedSides.add(pastedSide);
		}

		return validPastedSides;
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(PASTEDSIDES, EnumPasting.getPasting(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return ((EnumPasting)state.getValue(PASTEDSIDES)).ordinal();
	}

	@Override
	protected BlockState createBlockState() {
		return new BlockState(this, new IProperty[] { PASTEDSIDES, POWER });
	}

	private enum EnumPasting implements IStringSerializable {
		DUNSW(EnumFacing.EAST),
		DUNSE(EnumFacing.WEST),
		DUNWE(EnumFacing.SOUTH),
		DUSWE(EnumFacing.NORTH),
		DNSWE(EnumFacing.UP),
		UNSWE(EnumFacing.DOWN);

		private final EnumFacing[] sides;
		private final EnumSet<EnumFacing> sideSet;
		private final String name;

		private static final EnumPasting[] PASTING_LOOKUP = new EnumPasting[EnumPasting.values().length];

		private EnumPasting(EnumFacing missingSide) {
			EnumFacing[] sides = new EnumFacing[5];
			EnumSet<EnumFacing> sideSet = EnumSet.noneOf(EnumFacing.class);
			StringBuilder name = new StringBuilder();

			byte i = 0;
			for (EnumFacing side : EnumFacing.VALUES) {

				if (side != missingSide) {
					sides[i++] = side;
					sideSet.add(side);
					name.append(side.getName()).append(", ");
				}
			}

			this.sides = sides;
			this.sideSet = sideSet;
			this.name = name.substring(0, name.length() - 2);
		}

		@Override
		public String toString() {
			return this.getName();
		}

		@Override
		public String getName() {
			return this.name;
		}

		private static EnumPasting getPasting(int ordinal) {
			return PASTING_LOOKUP[ordinal];
		}

		static {
			for (EnumPasting pasting : EnumPasting.values())
				PASTING_LOOKUP[pasting.ordinal()] = pasting;
		}
	}
}
