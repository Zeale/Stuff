package zeale.apps.stuff.app.guis.windows.calculator.calculators.statistics;

enum ContProperty implements PropertyTemplate<ContProperty> {
	N("Count/Sample Size (n)"), SUM("Sum (\u03A3)"), SQUARE_OF_SUM("Square of Sum (\u03A3\u00B2)"),
	SUM_OF_SQUARES("Sum of Squares (\u03A3(x\u1D62)\u00B2)"), MEAN("Mean (x\u0305)"), MEDIAN("Median (x\u0303)"),
	MODE("Mode"), POPULATION_VARIANCE("Population Variance"),
	POPULATION_STANDARD_DEVIATION("Population Standard Deviation"), SAMPLE_VARIANCE("Sample Variance"),
	SAMPLE_STANDARD_DEVIATION("Sample Standard Deviation");

	private final String name;

	private ContProperty(String name) {
		this.name = name;
	}

	public void set(double val, Property... props) {
		set(String.valueOf(val), props);
	}

	@Override
	public String getName() {
		return name;
	}

}