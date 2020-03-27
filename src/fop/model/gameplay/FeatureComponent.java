package fop.model.gameplay;

import java.util.ArrayList;
import java.util.List;

import fop.Carcassonne;
import fop.base.Graph;
import fop.base.Node;
import fop.model.graph.FeatureNode;
import fop.model.player.Player;
import fop.model.tile.FeatureType;

public class FeatureComponent {

	private Gameboard gameboard;
	private Graph graph;
	private Node startNode;
	private List<Node<FeatureType>> nodeList;
	private List<Node<FeatureType>> nextNode;
	private List<Node<FeatureType>> visitedNodes;
	private List<Node<FeatureType>> allComponents;
	private List<Node<FeatureType>> hasMeepleOnIt;
	private int size;
	private boolean complete;
	private List<Player> players;
	private List<Integer> scores;
	private List<Player> scorer;
	
//	Zusammenhangskomponente wird erstellt
//	allComponents = alle Nodes in dieser
//	size = Anzahl Nodes, die drinne sind
//	scorer = Spieler, die Punkte erhalten
//	complete = ob die ZHK abgeschlossen ist (zB Stadt)
//	hasMeepleOnIt = alle Nodes mit Meeple darauf
	
	public FeatureComponent(Gameboard gameboard, List<Node<FeatureType>> nodeList, Node startNode, List<Player> players) {
		this.gameboard = gameboard;
		graph = gameboard.getGraph();
		this.nodeList = nodeList;
		this.startNode = startNode;
		complete = true;
		allComponents.add(startNode);
		this.players = players;
		setUp();
		calculatePlayerMeepleScore();
		determineWhoGetsPoints();
	}

	/**
	 * This method sets up the final scorer List, which represents Players that are to be rewarded
	 */
	private void determineWhoGetsPoints() {
		int toTake = 0;
		int comparison = 0;
		for(int i = 0; i < scores.size(); i++) {
			if(scores.get(i) < comparison) {
				players.set(i, null);
			}
			if(scores.get(i) > comparison) {
				players.set(toTake, null);
				toTake = i;
				comparison = scores.get(i);
			}
		}
		for(Player all : players) {
			if(all != null) {
				scorer.add(all);
			}
		}
	}

	/**
	 * This Method calculates the amount of meeple in the FeatureComponent for every Player and saves it into scores list
	 */
	private void calculatePlayerMeepleScore() {
		for(Node<FeatureType> all : allComponents) {
			FeatureNode node = (FeatureNode) all;
			if(node.hasMeeple()) {
				hasMeepleOnIt.add(node);
				Player p = node.getPlayer();
				for(int i = 0; i < players.size(); i++) {
					if(p == players.get(i)) {
						scores.set(i, (scores.get(i) + 1));
					}
				}
			}
		}
	}

	/**
	 * This Method creates a FeatureComponent which can be used to calculate the points
	 */
	public void setUp() {
		boolean nextNodeEmpty = false;
		while(nextNodeEmpty == false) {
			List<Node<FeatureType>> nextNodeTemp = new ArrayList<Node<FeatureType>>();
			for(Node all : nextNode) {
				boolean hasNeighbour = false;
				for(int i = 0; i < nodeList.size(); i++) {
					if(graph.getEdge(all, nodeList.get(i)) == null) {
					} else {
						hasNeighbour = true;
						if(!visitedNodes.contains(nodeList.get(i))) {
							size++;
							nextNodeTemp.add(nodeList.get(i));
							allComponents.add(nodeList.get(i));
						}
					}
				}
				if(hasNeighbour == false) {
					complete = false;
				}
			}
			nextNode = nextNodeTemp;
			if(nextNode.isEmpty()) {
				nextNodeEmpty = true;
			}
		}
	}

	/**
	 * @return the allComponents
	 */
	public List<Node<FeatureType>> getAllComponents() {
		return allComponents;
	}

	/**
	 * @return the size
	 */
	public int getSize() {
		return size;
	}

	/**
	 * @return the complete
	 */
	public boolean isComplete() {
		return complete;
	}	
	
	public List<Player> playersToReward() {
		return scorer;
	}
}
