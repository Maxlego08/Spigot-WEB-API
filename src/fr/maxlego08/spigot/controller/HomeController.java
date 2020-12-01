package fr.maxlego08.spigot.controller;

import java.io.IOException;
import java.io.OutputStream;

import fr.maxlego08.spigot.Controller;

public class HomeController extends Controller {

	public HomeController() {
		super("/");
	}

	@Override
	public void onRequest() throws IOException{

		String response = "Spigot API";
		exchange.sendResponseHeaders(200, response.getBytes().length);
		exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
		OutputStream os = exchange.getResponseBody();
		os.write(response.getBytes());
		os.close();		
		
	}

}
