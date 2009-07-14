package sc.logic;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Vector;

import sc.common.CouldNotFindAnyLanguageFileException;
import sc.common.CouldNotFindAnyPluginException;
import sc.common.IConfiguration;
import sc.gui.stuff.YearComparator;
import sc.guiplugin.interfaces.IGuiPluginHost;
import sc.guiplugin.interfaces.IObservation;
import sc.plugin.GUIPluginInstance;
import sc.plugin.GUIPluginManager;
import sc.server.Application;
import sc.server.Lobby;

public class LogicFacade implements ILogicFacade {

	/**
	 * Folder of all language files
	 */
	private static final String BASENAME = "sc/resource/game_gui";
	/**
	 * Configuration file name
	 */
	
	/**
	 * Holds all vailable plugins
	 */
	private final GUIPluginManager pluginMan;
	/**
	 * For multi-language support
	 */
	private ResourceBundle languageData;
	private IObservation observation;
	private Lobby server;

	/**
	 * Singleton instance
	 */
	private static volatile LogicFacade instance;

	private LogicFacade() { // Singleton
		this.pluginMan = GUIPluginManager.getInstance();
	}

	public static LogicFacade getInstance() {
		if (null == instance) {
			synchronized (LogicFacade.class) {
				if (null == instance) {
					instance = new LogicFacade();
				}
			}
		}
		return instance;
	}

	@Override
	public void loadLanguageData() throws CouldNotFindAnyLanguageFileException {
		ELanguage language = GUIConfiguration.instance().getLanguage();
		Locale locale;
		switch (language) {
		case DE:
			locale = new Locale("de", "DE");
			break;
		case EN:
			locale = new Locale("en", "EN");
			break;
		default:
			throw new CouldNotFindAnyLanguageFileException();
		}

		this.languageData = ResourceBundle.getBundle(BASENAME, locale);
	}

	@Override
	public GUIPluginManager getPluginManager() {
		return pluginMan;
	}

	@Override
	public void loadPlugins() throws CouldNotFindAnyPluginException {
		this.pluginMan.reload();
		if (this.pluginMan.getAvailablePlugins().size() == 0) {
			throw new CouldNotFindAnyPluginException();
		}
		this.pluginMan.activateAllPlugins(new IGuiPluginHost() {

		});
	}

	@Override
	public ResourceBundle getLanguageData() {
		return languageData;
	}

	@Override
	public IConfiguration getConfiguration() {
		return GUIConfiguration.instance();
	}

	@Override
	public IObservation getObservation() {
		return observation;
	}

	@Override
	public void setObservation(IObservation observer) {
		this.observation = observer;
	}

	@Override
	public void startServer(Integer port, boolean timeout) {
		if (null != server) {
			this.stopServer();
		}
		server = Application.startServer(port, timeout);
	}

	@Override
	public void stopServer() {
		if (null != observation) {
			observation.cancel();
		}
		if (server != null)
			server.close();
	}

	@Override
	public void unloadPlugins() {
		pluginMan.reload();
	}

	/**
	 * Returns available plugins in sorted order.
	 * 
	 * @return plugins
	 */
	public List<GUIPluginInstance> getAvailablePluginsSorted() {
		Collection<GUIPluginInstance> plugins = pluginMan.getAvailablePlugins();
		// sort by plugin's year
		List<GUIPluginInstance> sortedPlugins = new LinkedList<GUIPluginInstance>(
				plugins);
		Collections.sort(sortedPlugins, new YearComparator());
		return sortedPlugins;
	}

	/**
	 * Returns all available plugin names in a sorted order.
	 * 
	 * @param plugins
	 * @return
	 */
	public Vector<String> getPluginNames(List<GUIPluginInstance> plugins) {
		Vector<String> result = new Vector<String>();
		int last = 0;
		for (int i = 0; i < plugins.size(); i++) {
			GUIPluginInstance pluginInstance = plugins.get(i);
			if (pluginInstance.getPlugin().getPluginYear() > last) {
				result.add(pluginInstance.getDescription().name());
			}
		}

		return result;
	}

}
