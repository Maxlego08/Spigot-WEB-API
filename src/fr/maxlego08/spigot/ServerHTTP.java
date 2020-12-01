package fr.maxlego08.spigot;

import java.io.IOException;

import fr.maxlego08.spigot.controller.HomeController;
import fr.maxlego08.spigot.controller.SearchUserController;

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
		this.register(new SearchUserController());
	}

	@Override
	protected void onDisable() {
		
	}

}
