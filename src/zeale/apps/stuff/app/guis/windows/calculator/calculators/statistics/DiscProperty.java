package zeale.apps.stuff.app.guis.windows.calculator.calculators.statistics;

enum DiscProperty implements PropertyTemplate<DiscProperty> {
	;

	public final String name;

	private DiscProperty(String name) {
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