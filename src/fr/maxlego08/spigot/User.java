package fr.maxlego08.spigot;

public class User {

	private final int id;
	private final String name;

	public User(int id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

}
