package zeale.apps.stuff.app.guis.windows.calculator.calculators.statistics;

enum ContProperty implements PropertyTemplate<ContProperty> {
	SUM("Sum (\u03A3)"), SQUARE_OF_SUM("Square of Sums (\u03A3\u00B2)"),
	SUM_OF_SQUARES("Sum of Squares (\u03A3(x\u1D62)\u00B2)"), MEAN("Mean (x\u0305)"), MEDIAN("Median (x\u0303)"),
	MODE("Mode");

	private final String name;

	private ContProperty(String name) {
		this.name = name;
	}

	@Override
	public void set(String val, Property... props) {
		props[ordinal()].valueProperty().set(val);
	}

	@Override
	public String getName() {
		return name;
	}

}