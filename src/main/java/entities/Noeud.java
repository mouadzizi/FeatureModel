package entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)

public class Noeud {
	private String name;
	private String relationship;
	private Noeud noeuds[];
	private Noeud father;
	private Boolean mandatory;
	private boolean visited;
	public String getName() {
		return name;
	}
	public boolean getMandatory() {
		return mandatory;
	}
	public void setName(String name) {
		this.name = name;
	}

	public void setMendarory(Boolean mandatory) {
		this.mandatory = mandatory;
	}

	public String getRelationship() {
		return relationship;
	}
	public void setRelationship(String relationship) {
		this.relationship = relationship;
	}
	public Noeud[] getNoeuds() {
		return noeuds;
	}
	public void setNoeuds(Noeud[] noeuds) {
		this.noeuds = noeuds;
	}
	public Noeud getFather() {
		return father;
	}
	public void setFather(Noeud father) {
		this.father = father;
	}

	public boolean isVisited() {
		return visited;
	}

	public void setVisited(boolean visited) {
		this.visited = visited;
	}

	public Noeud(String name, String relationship, Noeud[] noeuds, Noeud father) {
		super();
		this.name = name;
		this.relationship = relationship;
		this.noeuds = noeuds;
		this.father = father;
	}
	
	public Noeud() {
		super();
	}

}
