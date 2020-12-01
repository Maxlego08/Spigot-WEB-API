package fr.maxlego08.spigot;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;

import be.maximvdw.spigotsite.api.SpigotSiteAPI;
import be.maximvdw.spigotsite.api.resource.ResourceManager;
import be.maximvdw.spigotsite.api.user.UserManager;

public abstract class Controller {

	protected Gson gson;
	protected SpigotSiteAPI spigotSiteAPI;
	protected UserManager userManager;
	protected ResourceManager resourceManager;
	protected Map<String, String> parameters;
	protected HttpExchange exchange;

	private final String url;

	/**
	 * 
	 * @param url
	 */
	public Controller(String url) {
		super();
		this.url = url;
	}

	/**
	 * 
	 * @param gson
	 * @param spigotSiteAPI
	 * @param resourceManager
	 * @param userManager
	 * @param exchange
	 */
	public void onPreRequest(Gson gson, SpigotSiteAPI spigotSiteAPI, ResourceManager resourceManager,
			UserManager userManager, HttpExchange exchange) {
		this.gson = gson;
		this.exchange = exchange;
		this.spigotSiteAPI = spigotSiteAPI;
		this.resourceManager = resourceManager;
		this.userManager = userManager;
		this.parameters = queryToMap(exchange.getRequestURI().getQuery());
	}

	/**
	 * 
	 * @param query
	 * @return
	 */
	private final Map<String, String> queryToMap(String query) {
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

	/**
	 * On request
	 */
	public abstract void onRequest() throws IOException;

	/**
	 * @return the gson
	 */
	public Gson getGson() {
		return gson;
	}

	/**
	 * @return the spigotSiteAPI
	 */
	public SpigotSiteAPI getSpigotSiteAPI() {
		return spigotSiteAPI;
	}

	/**
	 * @return the userManager
	 */
	public UserManager getUserManager() {
		return userManager;
	}

	/**
	 * @return the resourceManager
	 */
	public ResourceManager getResourceManager() {
		return resourceManager;
	}

	/**
	 * @return the getParameters
	 */
	public Map<String, String> getParameters() {
		return parameters;
	}

	/**
	 * @return the exchange
	 */
	public HttpExchange getExchange() {
		return exchange;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * Return error message
	 * 
	 * @param errorCode
	 * @param error
	 * @throws IOException
	 */
	protected void handleJsonError(int errorCode, String error) throws IOException {
		Map<String, String> errors = new HashMap<>();
		errors.put("error", error);
		String response = gson.toJson(errors);
		exchange.sendResponseHeaders(errorCode, response.getBytes().length);
		OutputStream os = exchange.getResponseBody();
		os.write(response.getBytes());
		os.close();
	}

	/**
	 * Return JSON response
	 * 
	 * @param httpCode
	 * @param object
	 * @throws IOException
	 */
	protected void handleJson(int httpCode, Object object) throws IOException {
		String response = gson.toJson(object);
		exchange.sendResponseHeaders(httpCode, response.getBytes().length);
		OutputStream os = exchange.getResponseBody();
		os.write(response.getBytes());
		os.close();
	}

}
