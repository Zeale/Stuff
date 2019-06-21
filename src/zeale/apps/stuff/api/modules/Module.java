package zeale.apps.stuff.api.modules;

public abstract class Module {
	/**
	 * Launches this module. This method is called each time the user launches this
	 * module on a new instance of this class, on the JavaFX Thread.
	 * 
	 * @throws Exception In case an exception occurs.
	 */
	public abstract void launch() throws Exception;
}
