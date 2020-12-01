package fr.maxlego08.spigot;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;

import be.maximvdw.spigotsite.SpigotSiteCore;
import be.maximvdw.spigotsite.api.SpigotSiteAPI;
import be.maximvdw.spigotsite.api.resource.ResourceManager;
import be.maximvdw.spigotsite.api.user.UserManager;
import fr.maxlego08.spigot.exception.ControllerAlreadyExistException;

public abstract class Server {

	private List<Controller> controllers = new ArrayList<>();
	private HttpServer httpServer;
	private final int port = 80;
	private boolean isEnable = false;

	protected final Gson gson;
	protected SpigotSiteAPI spigotSiteAPI;
	protected UserManager userManager;
	protected ResourceManager resourceManager;

	private boolean exist(Controller controller) {
		return controllers.stream().filter(c -> c.getUrl().equalsIgnoreCase(controller.getUrl())).findAny().isPresent();
	}

	public Server() throws IOException {

		long currentTimeMillis = System.currentTimeMillis();

		this.onLoad();

		// Gson
		gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().serializeNulls()
				.excludeFieldsWithModifiers(Modifier.TRANSIENT, Modifier.VOLATILE).create();

		System.out.println(String.format("Chargement du serveur web sur le port %s", port));
		httpServer = HttpServer.create(new InetSocketAddress(port), 0);

		// Chargement de l'API Spigot de façon asynchrome
		Thread thread = new Thread(() -> {
			System.out.println("Chargement de l'API spigot...");
			spigotSiteAPI = new SpigotSiteCore();
			System.out.println("Chargement des users...");
			userManager = spigotSiteAPI.getUserManager();
			System.out.println("Chargement des resources...");
			resourceManager = spigotSiteAPI.getResourceManager();

			isEnable = true;
		});
		thread.start();

		this.onEnable();

		for (Controller controller : this.controllers) {
			HttpContext context = httpServer.createContext(controller.getUrl());
			context.setHandler(exchange -> {
				controller.onPreRequest(gson, spigotSiteAPI, resourceManager, userManager, exchange);
				
				if (isEnable())
					controller.onRequest();
				else
					controller.handleJsonError(503, "Site launch in progress...");
			});
		}

		httpServer.start();

		Server server = this;
		Runtime.getRuntime().addShutdownHook(new Thread() {

			@Override
			public void run() {
				server.onDisable();
				if (httpServer != null)
					httpServer.stop(0);
			}

		});

		System.out.println(String.format("Chargement du serveur effectué en %sms",
				System.currentTimeMillis() - currentTimeMillis));
	}

	/**
	 * Register controller
	 * 
	 * @param controller
	 */
	protected void register(Controller controller) {

		if (exist(controller))
			throw new ControllerAlreadyExistException("Le controller " + controller.getUrl() + " existe déjà.");

		this.controllers.add(controller);
	}

	/**
	 * Unregister controller
	 * 
	 * @param controller
	 */
	protected void unregister(Controller controller) {
		this.controllers.remove(controller);
	}

	protected abstract void onLoad();

	protected abstract void onEnable();

	protected abstract void onDisable();

	public boolean isEnable() {
		return isEnable;
	}

}
