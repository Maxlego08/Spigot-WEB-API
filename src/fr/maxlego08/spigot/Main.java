package fr.maxlego08.spigot;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Modifier;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import be.maximvdw.spigotsite.SpigotSiteCore;
import be.maximvdw.spigotsite.api.exceptions.ConnectionFailedException;
import be.maximvdw.spigotsite.api.user.User;
import be.maximvdw.spigotsite.api.user.UserManager;

public class Main {

	private final Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().serializeNulls()
			.excludeFieldsWithModifiers(Modifier.TRANSIENT, Modifier.VOLATILE).create();
	private final UserManager manager;
	private boolean isEnable = false;

	public static void main(String[] args) {
		try {
			new Main();
		} catch (IOException e) {
			System.out.println("Error 2 !");
			e.printStackTrace();
		}

	}

	public Main() throws IOException {

		isEnable = false;
		System.out.println("Lancement du serveur web...");
		HttpServer server = HttpServer.create(new InetSocketAddress(80), 0);
		HttpContext context = server.createContext("/");
		context.setHandler(e -> {
			try {
				handleRequest(e);
			} catch (ConnectionFailedException e1) {
				e1.printStackTrace();
			}
		});
		server.start();

		System.out.println("Lancement de spigotAPI...");
		SpigotSiteCore siteCore = new SpigotSiteCore();
		manager = siteCore.getUserManager();

		System.out.println("Site opérationnel !");
		isEnable = true;

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				server.stop(0);
			}
		});

	}

	private void handleRequest(HttpExchange exchange) throws IOException, ConnectionFailedException {

		if (!isEnable) {
			handleError(exchange);
			return;
		}

		Map<String, String> map;
		try {
			map = queryToMap(exchange.getRequestURI().getQuery());
		} catch (Exception e) {
			e.printStackTrace();
			empty(exchange);
			return;
		}

		if (map == null) {
			empty(exchange);
			return;
		}

		if (!map.containsKey("name")) {
			empty(exchange);
			return;
		}

		String name = map.get("name");

		String response = "";
		try {
			User user = manager.getUserByName(name);
			if (user == null) {
				invalidParameters(exchange, name);
				return;
			}
			response = gson.toJson(user);
		} catch (Exception e) {
			invalidParameters(exchange, name);
			return;
		}

		exchange.sendResponseHeaders(200, response.getBytes().length);
		OutputStream os = exchange.getResponseBody();
		os.write(response.getBytes());
		os.close();
	}

	private void handleError(HttpExchange exchange) throws IOException {
		Map<String, String> errors = new HashMap<>();
		errors.put("error", "Site launch in progress...");
		String response = gson.toJson(errors);
		exchange.sendResponseHeaders(300, response.getBytes().length);
		OutputStream os = exchange.getResponseBody();
		os.write(response.getBytes());
		os.close();
	}

	private void invalidParameters(HttpExchange exchange, String user) throws IOException {
		Map<String, String> errors = new HashMap<>();
		errors.put("error", "Unable to find the user " + user);
		String response = gson.toJson(errors);
		exchange.sendResponseHeaders(404, response.getBytes().length);
		OutputStream os = exchange.getResponseBody();
		os.write(response.getBytes());
		os.close();
	}

	private void empty(HttpExchange exchange) throws IOException {
		Map<String, String> errors = new HashMap<>();
		errors.put("error", "Bad request");
		String response = gson.toJson(errors);
		exchange.sendResponseHeaders(400, response.getBytes().length);
		OutputStream os = exchange.getResponseBody();
		os.write(response.getBytes());
		os.close();
	}

	public Map<String, String> queryToMap(String query) {
		Map<String, String> result = new HashMap<>();
		if (query == null)
			return result;
		for (String param : query.split("&")) {
			String[] entry = param.split("=");
			if (entry.length > 1) {
				result.put(entry[0], entry[1]);
			} else {
				result.put(entry[0], "");
			}
		}
		return result;
	}

}
