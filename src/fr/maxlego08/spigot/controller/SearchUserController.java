package fr.maxlego08.spigot.controller;

import java.io.IOException;

import be.maximvdw.spigotsite.api.exceptions.ConnectionFailedException;
import be.maximvdw.spigotsite.api.user.User;
import fr.maxlego08.spigot.Controller;

public class SearchUserController extends Controller {

	public SearchUserController() {
		super("/search/users");
	}

	@Override
	public void onRequest() throws IOException {

		if (!parameters.containsKey("name"))
			this.handleJsonError(400, "Bad request");
		else {

			String name = parameters.get("name");
			try {
				User user = userManager.getUserByName(name);
				if (user == null)
					this.handleJsonError(404, String.format("user %s was not found", name));
				else
					this.handleJson(200, user);

			} catch (ConnectionFailedException e) {
				this.handleJsonError(404, String.format("user %s was not found", name));
				e.printStackTrace();
			}

		}

	}

}
