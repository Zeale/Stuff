package zeale.apps.stuff.app.guis.windows.calculator.calculators.statistics;

enum DiscProperty implements PropertyTemplate<DiscProperty> {
	N("Count/Sample Size (n)",
			"This is the number of values contained in your sample (in the list of numbers you entered).");

	public final String name;
	private final String tooltip;

	private DiscProperty(String name, String tooltip) {
		this.name = name;
		this.tooltip = tooltip;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String tooltip() {
		return tooltip;
	}

}