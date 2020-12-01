package fr.maxlego08.spigot;

import java.io.IOException;

import fr.maxlego08.spigot.controller.HomeController;

public class ServerHTTP extends Server{

	public ServerHTTP() throws IOException {
		super();
	}

	@Override
	protected void onLoad() {
		
	}

	@Override
	protected void onEnable() {
		this.register(new HomeController());
	}

	@Override
	protected void onDisable() {
		
	}

}
