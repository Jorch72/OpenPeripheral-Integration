package openperipheral.integration.forestry;

import java.util.Map;

import openperipheral.api.ITypeConverter;
import openperipheral.api.ITypeConvertersRegistry;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import forestry.api.apiculture.*;
import forestry.api.arboriculture.*;
import forestry.api.genetics.*;
import forestry.api.lepidopterology.*;

public class ConverterIIndividual implements ITypeConverter {

	@Override
	public Object fromLua(ITypeConvertersRegistry registry, Object obj, Class<?> expected) {
		return null;
	}

	private abstract static class GenomeAccess {
		public IAllele getAllele(IGenome genome, int chromosome) {
			IChromosome[] genotype = genome.getChromosomes();
			IChromosome ch = genotype[chromosome];
			if (ch == null) return null;
			return getAllele(ch);
		}

		protected abstract IAllele getAllele(IChromosome chromosome);
	}

	private static final GenomeAccess ACTIVE = new GenomeAccess() {
		@Override
		protected IAllele getAllele(IChromosome chromosome) {
			return chromosome.getActiveAllele();
		}
	};

	private static final GenomeAccess INACTIVE = new GenomeAccess() {
		@Override
		protected IAllele getAllele(IChromosome chromosome) {
			return chromosome.getInactiveAllele();
		}
	};

	private interface IAlleleConverter<A extends IAllele> {
		public Object convert(A allele);
	}

	private static final Map<Class<? extends IAllele>, IAlleleConverter<?>> conventers =
			ImmutableMap.<Class<? extends IAllele>, IAlleleConverter<?>> builder()
					.put(IAlleleSpecies.class, new IAlleleConverter<IAlleleSpecies>() {

						@Override
						public Object convert(IAlleleSpecies allele) {
							Map<String, Object> result = Maps.newHashMap();
							result.put("authority", allele.getAuthority());
							result.put("binomialName", allele.getBinomial());
							result.put("complexity", allele.getComplexity());
							result.put("humidity", allele.getHumidity());
							result.put("name", allele.getName());
							result.put("temperature", allele.getTemperature());
							return result;
						}

					})
					.put(IAlleleFloat.class, new IAlleleConverter<IAlleleFloat>() {
						@Override
						public Object convert(IAlleleFloat allele) {
							return allele.getValue();
						}
					})
					.put(IAlleleInteger.class, new IAlleleConverter<IAlleleInteger>() {
						@Override
						public Object convert(IAlleleInteger allele) {
							return allele.getValue();
						}
					})
					.put(IAlleleBoolean.class, new IAlleleConverter<IAlleleBoolean>() {
						@Override
						public Object convert(IAlleleBoolean allele) {
							return allele.getValue();
						}
					})
					.put(IAlleleArea.class, new IAlleleConverter<IAlleleArea>() {
						@Override
						public Object convert(IAlleleArea allele) {
							return allele.getValue();
						}
					})
					.put(IAllelePlantType.class, new IAlleleConverter<IAllelePlantType>() {
						@Override
						public Object convert(IAllelePlantType allele) {
							return allele.getPlantTypes();
						}
					})
					.put(IAlleleGrowth.class, new IAlleleConverter<IAlleleGrowth>() {
						@Override
						public Object convert(IAlleleGrowth allele) {
							return allele.getProvider().getInfo();
						}
					})
					.build();

	private abstract static class GenomeReader<G extends IGenome, E extends Enum<E> & IChromosomeType> {
		private final G genome;

		public GenomeReader(G genome) {
			this.genome = genome;
		}

		@SuppressWarnings("unchecked")
		protected <A extends IAllele> A getAllele(GenomeAccess access, Class<A> cls, E chromosome) {
			Preconditions.checkArgument(cls.isAssignableFrom(chromosome.getAlleleClass()));
			IAllele allele = access.getAllele(genome, chromosome.ordinal());
			return (A)allele;
		}

		protected <A extends IAllele> Object convertAllele(GenomeAccess access, Class<A> cls, E chromosome) {
			A allele = getAllele(access, cls, chromosome);
			if (allele == null) return "missing";
			@SuppressWarnings("unchecked")
			IAlleleConverter<IAllele> conventer = (IAlleleConverter<IAllele>)conventers.get(cls);
			return conventer != null? conventer.convert(allele) : allele.getName();
		}

		protected abstract void addAlleleInfo(GenomeAccess access, Map<String, Object> result);

		public Map<String, Object> getActiveInfo() {
			Map<String, Object> result = Maps.newHashMap();
			addAlleleInfo(ACTIVE, result);
			return result;
		}

		public Map<String, Object> getInactiveInfo() {
			Map<String, Object> result = Maps.newHashMap();
			addAlleleInfo(INACTIVE, result);
			return result;
		}
	}

	private static class BeeGenomeReader extends GenomeReader<IBeeGenome, EnumBeeChromosome> {

		public BeeGenomeReader(IBeeGenome genome) {
			super(genome);
		}

		@Override
		protected void addAlleleInfo(GenomeAccess access, Map<String, Object> result) {
			result.put("species", convertAllele(access, IAlleleSpecies.class, EnumBeeChromosome.SPECIES));
			result.put("speed", convertAllele(access, IAlleleFloat.class, EnumBeeChromosome.SPEED));
			result.put("lifespan", convertAllele(access, IAlleleInteger.class, EnumBeeChromosome.LIFESPAN));
			result.put("fertility", convertAllele(access, IAlleleInteger.class, EnumBeeChromosome.FERTILITY));
			result.put("temperatureTolerance", convertAllele(access, IAlleleTolerance.class, EnumBeeChromosome.TEMPERATURE_TOLERANCE));
			result.put("nocturnal", convertAllele(access, IAlleleBoolean.class, EnumBeeChromosome.NOCTURNAL));
			result.put("humidityTolerance", convertAllele(access, IAlleleTolerance.class, EnumBeeChromosome.HUMIDITY_TOLERANCE));
			result.put("tolerantFlyer", convertAllele(access, IAlleleBoolean.class, EnumBeeChromosome.TOLERANT_FLYER));
			result.put("caveDwelling", convertAllele(access, IAlleleBoolean.class, EnumBeeChromosome.CAVE_DWELLING));
			result.put("flowerProvider", convertAllele(access, IAlleleFlowers.class, EnumBeeChromosome.FLOWER_PROVIDER));
			result.put("flowering", convertAllele(access, IAlleleInteger.class, EnumBeeChromosome.FLOWERING));
			result.put("effect", convertAllele(access, IAlleleBeeEffect.class, EnumBeeChromosome.EFFECT));
			result.put("territory", convertAllele(access, IAlleleArea.class, EnumBeeChromosome.TERRITORY));
		}
	}

	private static class ButterflyGenomeReader extends GenomeReader<IButterflyGenome, EnumButterflyChromosome> {

		public ButterflyGenomeReader(IButterflyGenome genome) {
			super(genome);
		}

		@Override
		protected void addAlleleInfo(GenomeAccess access, Map<String, Object> result) {
			result.put("species", convertAllele(access, IAlleleSpecies.class, EnumButterflyChromosome.SPECIES));
			result.put("size", convertAllele(access, IAlleleFloat.class, EnumButterflyChromosome.SIZE));
			result.put("speed", convertAllele(access, IAlleleFloat.class, EnumButterflyChromosome.SPEED));
			result.put("lifespan", convertAllele(access, IAlleleInteger.class, EnumButterflyChromosome.LIFESPAN));
			result.put("metabolism", convertAllele(access, IAlleleInteger.class, EnumButterflyChromosome.METABOLISM));
			result.put("fertility", convertAllele(access, IAlleleInteger.class, EnumButterflyChromosome.FERTILITY));
			result.put("temperatureTolerance", convertAllele(access, IAlleleTolerance.class, EnumButterflyChromosome.TEMPERATURE_TOLERANCE));
			result.put("humidityTolerance", convertAllele(access, IAlleleTolerance.class, EnumButterflyChromosome.HUMIDITY_TOLERANCE));
			result.put("nocturnal", convertAllele(access, IAlleleBoolean.class, EnumButterflyChromosome.NOCTURNAL));
			result.put("tolerantFlyer", convertAllele(access, IAlleleBoolean.class, EnumButterflyChromosome.TOLERANT_FLYER));
			result.put("fireResist", convertAllele(access, IAlleleBoolean.class, EnumButterflyChromosome.FIRE_RESIST));
			result.put("flowerProvider", convertAllele(access, IAlleleFlowers.class, EnumButterflyChromosome.FLOWER_PROVIDER));
			result.put("effect", convertAllele(access, IAlleleButterflyEffect.class, EnumButterflyChromosome.EFFECT));
			result.put("territory", convertAllele(access, IAlleleArea.class, EnumButterflyChromosome.TERRITORY));
		}
	}

	private static class TreeGenomeReader extends GenomeReader<ITreeGenome, EnumTreeChromosome> {

		public TreeGenomeReader(ITreeGenome genome) {
			super(genome);
		}

		@Override
		protected void addAlleleInfo(GenomeAccess access, Map<String, Object> result) {
			result.put("species", convertAllele(access, IAlleleSpecies.class, EnumTreeChromosome.SPECIES));
			result.put("growth", convertAllele(access, IAlleleGrowth.class, EnumTreeChromosome.GROWTH));
			result.put("height", convertAllele(access, IAlleleFloat.class, EnumTreeChromosome.HEIGHT));
			result.put("fertility", convertAllele(access, IAlleleFloat.class, EnumTreeChromosome.FERTILITY));
			result.put("fruits", convertAllele(access, IAlleleFruit.class, EnumTreeChromosome.FRUITS));
			result.put("yield", convertAllele(access, IAlleleFloat.class, EnumTreeChromosome.YIELD));
			result.put("plant", convertAllele(access, IAllelePlantType.class, EnumTreeChromosome.PLANT));
			result.put("sappiness", convertAllele(access, IAlleleFloat.class, EnumTreeChromosome.SAPPINESS));
			result.put("territory", convertAllele(access, IAlleleArea.class, EnumTreeChromosome.TERRITORY));
			result.put("effect", convertAllele(access, IAlleleLeafEffect.class, EnumTreeChromosome.EFFECT));
			result.put("maturation", convertAllele(access, IAlleleInteger.class, EnumTreeChromosome.MATURATION));
			result.put("girth", convertAllele(access, IAlleleInteger.class, EnumTreeChromosome.GIRTH));
		}
	}

	@Override
	public Object toLua(ITypeConvertersRegistry registry, Object obj) {
		if (obj instanceof IIndividual) {
			IIndividual individual = (IIndividual)obj;
			Map<String, Object> map = describeIndividual(individual);
			return registry.toLua(map);
		}
		return null;
	}

	public static Map<String, Object> describeIndividual(IIndividual individual) {
		Map<String, Object> map = Maps.newHashMap();
		map.put("displayName", individual.getDisplayName());
		map.put("ident", individual.getIdent());

		final boolean isAnalyzed = individual.isAnalyzed();
		map.put("isAnalyzed", isAnalyzed);
		map.put("isSecret", individual.isSecret());
		GenomeReader<?, ?> genomeReader = null;

		if (individual instanceof IIndividualLiving) {
			IIndividualLiving living = (IIndividualLiving)individual;
			map.put("health", living.getHealth());
			map.put("maxHealth", living.getMaxHealth());
		}

		if (individual instanceof IBee) {
			IBee bee = (IBee)individual;
			map.put("type", "bee");
			map.put("canSpawn", bee.canSpawn());
			map.put("generation", bee.getGeneration());
			map.put("hasEffect", bee.hasEffect());
			map.put("isAlive", bee.isAlive());
			map.put("isIrregularMating", bee.isIrregularMating());
			map.put("isNatural", bee.isNatural());

			if (isAnalyzed) genomeReader = new BeeGenomeReader(bee.getGenome());
		} else if (individual instanceof IButterfly) {
			IButterfly butterfly = (IButterfly)individual;
			map.put("type", "butterfly");
			map.put("size", butterfly.getSize());
			if (isAnalyzed) genomeReader = new ButterflyGenomeReader(butterfly.getGenome());
		} else if (individual instanceof ITree) {
			ITree tree = (ITree)individual;
			map.put("type", "tree");
			map.put("plantType", tree.getPlantTypes().toString());
			if (isAnalyzed) genomeReader = new TreeGenomeReader(tree.getGenome());
		}

		if (genomeReader != null) {
			map.put("active", genomeReader.getActiveInfo());
			map.put("inactive", genomeReader.getInactiveInfo());
		}
		return map;
	}

}
