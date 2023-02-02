package entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)

public class Root {
	private String name;
	private Noeud noeud;
	//private Constraints constraints;


	public Root() {
		super();
	}

	public Root(String name, String relationship, Noeud noeud, Constraints constraints) {
		super();
		this.name = name;
		this.noeud = noeud;
		//this.constraints = constraints;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Noeud getNoeud() {
		return noeud;
	}
	public void setNoeud(Noeud noeud) {
		this.noeud = noeud;
	}

	public Noeud getNoeudByName(Noeud noeud, String name) {
		assert noeud != null : "Noeud root cannot be null";
		if (noeud.getName().equals(name)) {
			return noeud;
		}
		if (noeud.getNoeuds() == null || noeud.getNoeuds().length == 0) return null;
		for (Noeud child : noeud.getNoeuds()) {
			Noeud result = getNoeudByName(child, name);
			if (result != null) {
				return result;
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return "the name of file is : " + name;
	}
}