package zeale.apps.stuff.app.guis.windows.calculator.calculators.statistics;

enum ContProperty implements PropertyTemplate<ContProperty> {
	N("Count/Sample Size (n)",
			"This is the number of values contained in your sample (in the list of numbers you entered)."),
	SUM("Sum (\u03A3)", "This is the result of adding all of the numbers in your sample together."),
	SQUARE_OF_SUM("Square of Sum (\u03A3\u00B2)", "This is simply the Sum, multiplied by itself. (\u03a3 * \u03a3)"),
	SUM_OF_SQUARES("Sum of Squares (\u03A3(x\u1D62)\u00B2)",
			"This sounds similar to the Square of Sum, but is actually the sum of each value in your sample squared. To get this value, every single number you entered is multiplied by itself, and then all of the results are added together. The squaring happens before the addition process."),
	MEAN("Mean (x\u0305)",
			"Also known as the average, the Mean is the result of adding all the values in your sample together and then dividing the result by the total number of values in your sample. (\u03a3 / n)"),
	MEDIAN("Median (x\u0303)",
			"This is the \"middle\" value. To get the mean, first your sample was sorted in increasing order. Then, if the size of your sample (n) was odd, the middle value was picked; that is the median. If the size was even, however, the two middle values were taken and averaged together to get the median."),
	MODE("Mode",
			"This is the most commonly occurring value. If more than one number occurred \"most common\"ly, then more than one value will be the mode. If your sample is [1, 1, 2, 3, 3, 4], then the numbers 1 and 3 will be the modes, since they both occur twice in your sample, and no other number occurs more than twice."),
	POPULATION_VARIANCE("Population Variance",
			"The Population Variance is a measurement of how much the values in your sample \"vary.\" It's formula is given by: \u03a3(x - x\u0305) / n. This number represents the average distance of the numbers in your sample from their mean (x\u0305), and can be used to measure how spread out or \"crazy\" the values in your sample can be."),
	POPULATION_STANDARD_DEVIATION("Population Standard Deviation",
			"This value is the square root of the Population Variance. The standard deviation is a more commonly used measurement of the variation in a dataset than is the variance. Contrastingly, it is not \"additive,\" as values of variance are."),
	SAMPLE_VARIANCE("Sample Variance",
			"This value is similar to the Population Variance, but is a measurement based off of your sample. One of the main purposes of Statistics is to be able to take a few values from a large \"population\" and be able to make good guesses about the population based off of only what you can measure in your sample. Sample Variance is a measurement obtained from your sample, but meant to represent the original population that the sample was taken from. The formula for Sample Variance is: \u03a3(x - x\u0305) / (n - 1). See the description for Population Variance for information on what variance is."),
	SAMPLE_STANDARD_DEVIATION("Sample Standard Deviation",
			"The Sample Standard Deviation is the standard deviation derived from the Sample Variance value. Standard Deviation is a useful statistic for determining how much a sample's values vary. See the description for Population Standard Deviation for a little bit of more information on standard deviations.");

	private final String name;
	private final String tooltip;

	@Override
	public String tooltip() {
		return tooltip;
	}

	private ContProperty(String name) {
		this(name, null);
	}

	private ContProperty(String name, String tooltip) {
		this.name = name;
		this.tooltip = tooltip;
	}

	public void set(double val, Property... props) {
		set(String.valueOf(val), props);
	}

	@Override
	public String getName() {
		return name;
	}

}