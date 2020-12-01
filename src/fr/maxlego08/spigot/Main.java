package fr.maxlego08.spigot;

import java.io.IOException;

public class Main {
	
	public static void main(String[] args) {
		try {
			new ServerHTTP();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
