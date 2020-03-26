package fop.model.gameplay;

import static fop.model.tile.FeatureType.CASTLE;
import static fop.model.tile.FeatureType.FIELDS;
import static fop.model.tile.FeatureType.MONASTERY;
import static fop.model.tile.FeatureType.ROAD;
import static fop.model.tile.Position.BOTTOM;
import static fop.model.tile.Position.BOTTOMLEFT;
import static fop.model.tile.Position.BOTTOMRIGHT;
import static fop.model.tile.Position.LEFT;
import static fop.model.tile.Position.RIGHT;
import static fop.model.tile.Position.TOP;
import static fop.model.tile.Position.TOPLEFT;
import static fop.model.tile.Position.TOPRIGHT;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import fop.base.Edge;
import fop.base.Node;
import fop.model.graph.FeatureGraph;
import fop.model.graph.FeatureNode;
import fop.model.player.Player;
import fop.model.tile.FeatureType;
import fop.model.tile.Position;
import fop.model.tile.Tile;

public class Gameboard extends Observable<Gameboard> {

	private Tile[][] board;
	private List<Tile> tiles;
	private FeatureGraph graph;
	private Tile newestTile;

	public Gameboard() {
		board = new Tile[144][144];
		tiles = new LinkedList<Tile>();
		graph = new FeatureGraph();
	}

	// kann nicht im konstrukor erfolgen, weil erst observer gesetzt werden muss
	public void initGameboard(Tile t) {
		newTile(t, 72, 72);
	}

	public void newTile(Tile t, int x, int y) {
		t.x = x;
		t.y = y;
		board[x][y] = newestTile = t;
		tiles.add(t);

		connectNodes(x, y);
		push(this); // pushes the new gameboard state to its observers (= GameBoardPanel)
	}

	/**
	 * Connects the nodes of all neighboring tiles facing the tile at given
	 * coordinates x, y. It is assumed that the tile is placed according to the
	 * rules.
	 * 
	 * @param x coordinate
	 * @param y coordinate
	 */
	private void connectNodes(int x, int y) {
		
		graph.addAllNodes(board[x][y].getNodes());
		graph.addAllEdges(board[x][y].getEdges());

		Tile t = board[x][y];
		
		boolean topcheck=false;
		boolean leftcheck=false;
		boolean rightcheck=false;
		boolean bottomcheck=false;

		// Check top tile
		// TODO
			// This might be helpful:
			// As we already ensured that the tile on top exists and fits the tile at x, y,
			// we know that if the feature of its top is a ROAD, the feature at the bottom
			// of the tile on top is a ROAD aswell. As every ROAD has FIELD nodes as
			// neighbours on both sides, we can connect those nodes of the two tiles. The
			// same logic applies to the next three routines.
		if(y-1>=0 && board[x][y-1]!=null)topcheck=true;//dummy check,we have to give something after if
		
	     if(topcheck && board[x][y-1].getNode(BOTTOM).getType()==t.getNode(TOP).getType())
	      
	    	 graph.addEdge(t.getNode(TOP),board[x][y-1].getNode(BOTTOM));
	     
	     if(topcheck && board[x][y-1].getNode(BOTTOMRIGHT)!=null  &&  t.getNode(TOPRIGHT)!=null && board[x][y-1].featureAtPosition(BOTTOMRIGHT)==t.featureAtPosition(TOPRIGHT))
				graph.addEdge(t.getNode(TOPRIGHT),board[x][y-1].getNode(BOTTOMRIGHT));
	     if(topcheck && board[x][y-1].getNode(BOTTOMLEFT)!=null &&  t.getNode(BOTTOMRIGHT)!=null  && board[x][y-1].featureAtPosition(BOTTOMLEFT)==t.featureAtPosition(TOPLEFT))
				graph.addEdge(t.getNode(TOPLEFT),board[x][y-1].getNode(BOTTOMLEFT));
	    	 
	      
	     
		// Check left tile
		// TODO
		if(x-1>=0 && board[x-1][y]!=null)leftcheck=true;
		
		if(leftcheck && board[x-1][y].getNode(RIGHT).getType()==t.getNode(LEFT).getType())
			graph.addEdge(t.getNode(LEFT),board[x-1][y].getNode(RIGHT));
		if(leftcheck && board[x-1][y].getNode(TOPRIGHT)!=null &&  t.getNode(TOPLEFT)!=null && board[x-1][y].featureAtPosition(TOPRIGHT)==t.featureAtPosition(TOPLEFT))
			graph.addEdge(t.getNode(TOPLEFT),board[x-1][y].getNode(TOPRIGHT));
		if(leftcheck && board[x-1][y].getNode(BOTTOMRIGHT)!=null &&  t.getNode(BOTTOMLEFT)!=null && board[x-1][y].featureAtPosition(BOTTOMRIGHT)==t.featureAtPosition(BOTTOMLEFT))
			graph.addEdge(t.getNode(BOTTOMLEFT),board[x-1][y].getNode(BOTTOMRIGHT));
		


		// Check right tile
		// TODO
		if(x+1<board.length && board[x+1][y]!=null)rightcheck=true;
		
		if(rightcheck && board[x+1][y].getNode(LEFT).getType()==t.getNode(RIGHT).getType())
			graph.addEdge(t.getNode(RIGHT),board[x+1][y].getNode(LEFT));
		if(rightcheck && board[x+1][y].getNode(TOPLEFT)!=null &&  t.getNode(TOPRIGHT)!=null && board[x+1][y].featureAtPosition(TOPLEFT)==t.featureAtPosition(TOPRIGHT))
			graph.addEdge(t.getNode(TOPRIGHT),board[x+1][y].getNode(TOPLEFT));
		if(rightcheck && board[x+1][y].getNode(BOTTOMLEFT)!=null &&  t.getNode(BOTTOMRIGHT)!=null && board[x+1][y].featureAtPosition(BOTTOMLEFT)==t.featureAtPosition(BOTTOMRIGHT))
			graph.addEdge(t.getNode(BOTTOMRIGHT),board[x+1][y].getNode(BOTTOMLEFT));


		// Check bottom tile
		// TODO
		if(y+1<board[0].length && board[x][y+1]!=null)bottomcheck=true;
		if(bottomcheck && board[x][y+1].getNode(TOP).getType()==t.getNode(BOTTOM).getType())
			graph.addEdge(t.getNode(BOTTOM),board[x][y+1].getNode(TOP));
		if(bottomcheck && board[x][y+1].getNode(TOPLEFT)!=null && t.getNode(TOPRIGHT)!=null&& board[x][y+1].featureAtPosition(TOPLEFT)==t.featureAtPosition(TOPRIGHT))
			graph.addEdge(t.getNode(TOPRIGHT),board[x][y+1].getNode(TOPLEFT));
		if(bottomcheck && board[x][y+1].getNode(TOPRIGHT)!=null  && t.getNode(TOPLEFT)!=null && board[x][y+1].featureAtPosition(TOPRIGHT)==t.featureAtPosition(TOPLEFT))
			graph.addEdge(t.getNode(TOPLEFT),board[x][y+1].getNode(TOPRIGHT));
			

	}

	/**
	 * This method checks if the spot where the given Tile shall be placed in is
	 * surrounded by only null and therefore invalid
	 * 
	 * @param t Given Tile object for which check shall be performed
	 * @param x x coordinate of the given Tile
	 * @param y x coordinate of the given Tile
	 * @return true if all surrounding spots are null, false if not
	 */
	private boolean checkSurroundedByNull(Tile t, int x, int y) {
		boolean allNull = true;
		
		//edges of gameboard treatment
		boolean dontCheck0 = x - 1 < 0;
		boolean dontCheck1 = x + 1 > 143;
		boolean dontCheck2 = y - 1 < 0;
		boolean dontCheck3 = y + 1 > 143;

		if (board[x - 1][y] != null && dontCheck0 == false) {

		}
		if (board[x + 1][y] != null && dontCheck1 == false) {

		}
		if (board[x][y - 1] != null && dontCheck2 == false) {

		}
		if (board[x][y + 1] != null && dontCheck3 == false) {

		}
		allNull = false;
		return allNull;

	}

	/**
	 * Checks if the given tile could be placed at position x, y on the board
	 * according to the rules.
	 * 
	 * @param t The tile
	 * @param x The x position on the board
	 * @param y The y position on the board
	 * @return True if it would be allowed, false if not.
	 */
	public boolean isTileAllowed(Tile t, int x, int y) {

		
		boolean topcheck=true;
		boolean leftcheck=true;
		boolean rightcheck=true;
		boolean bottomcheck=true;
		
		
		
 
		
		// Check top tile
		// TODO
		if(y-1<0)topcheck=true;		
		else if(board[x][y-1]!=null) {
		if(board[x][y-1].getNode(BOTTOM).getType()!=t.getNode(TOP).getType())topcheck= false;

		}
		// Check left tile
		// TODO
		
        if(x-1<0)leftcheck=true;        
		else if(board[x-1][y]!=null) {
		if(board[x-1][y].getNode(RIGHT).getType()!=t.getNode(LEFT).getType())leftcheck=false;
		}
        // Check right tile
		// TODO
		if(x+1>board.length)rightcheck=true;		
		else if(board[x+1][y]!=null) {
		if(board[x+1][y].getNode(LEFT).getType()!=t.getNode(RIGHT).getType())rightcheck=false;

		}

		// Check bottom tile
		// TODO
		if(y+1>board[0].length)bottomcheck=true;		
		else if(board[x][y+1]!=null) {
		if(board[x][y+1].getNode(TOP).getType()!=t.getNode(BOTTOM).getType())bottomcheck=false;
		}
		
		if(topcheck && leftcheck && rightcheck && bottomcheck) return true;
		
		 
		return false;
		
		
		
		
		
	}
	/**
	 * Checks if the given tile would be allowed anywhere on the board adjacent to
	 * other tiles and according to the rules.
	 * 
	 * @param newTile The tile.
	 * @return True if it is allowed to place the tile somewhere on the board, false
	 *         if not.
	 */
	public boolean isTileAllowedAnywhere(Tile newTile) {
		// Iterate over all tiles
		for(int x=0;x<board.length;x++) {
			for(int y=0;y<board[0].length;y++) {
				
				if(board[x][y]!=null)continue;
				// check top
				// TODO
				if(isTileAllowed(newTile, x, y))return true;

				// check left
				// TODO
				newTile.rotateRight();
				if(isTileAllowed(newTile, x, y))return true;

				// check right
				// TODO
				newTile.rotateRight();
				if(isTileAllowed(newTile, x, y))return true;

				// check bottom
				// TODO
				newTile.rotateRight();
				if(isTileAllowed(newTile, x, y))return true;
				
				newTile.rotateRight();
			
			}
		
		
	}
		
			
		
		// no valid position was found
		return false;
	}

	/**
	 * Calculates points for monasteries (one point for the monastery and one for
	 * each adjacent tile).
	 */
	public void calculateMonasteries(State state) {
		// the methods getNode() and getType of class Tile and FeatureNode might be
		// helpful

		// Check all surrounding tiles and add the points

		// Points are given if the landscape is complete or the game is over
		// Meeples are just returned in case of state == State.GAME_OVER
		
    //What is that mean? why should we set the score to 1 ,and which socre? we also dont have such a methode to change the value of socre 
		// After adding the points to the overall points of the player, set the score to
		// 1 again
		
		if(state == State.GAME_OVER) {
			
			for(int x =0;x<board.length;x++) {
				for(int y=0;y<board[0].length;y++) {
					
					
					if(board[x][y].featureAtPosition(Position.CENTER)==FeatureType.MONASTERY && board[x][y].hasMeeple()) {
						
							Player a =board[x][y].getMeeple();
							int score=0;
							
							
							for(int i =0;i<3;i++) {
								for(int j =0;j<3;j++) {
									
									if(board[x-1+i][y-1+j]!=null)score++;
								}
							}
							if(score!=9)a.addScore(score);
							a.returnMeeple();
							
						
					}
						
				}
			}
			
		}
                if(state != State.GAME_OVER) {
			
			for(int x =0;x<board.length;x++) {
				for(int y=0;y<board[0].length;y++) {
					
					
					if(board[x][y].featureAtPosition(Position.CENTER)==FeatureType.MONASTERY && board[x][y].hasMeeple()) {
						
							Player a =board[x][y].getMeeple();
							boolean complete =false;
							
							
							
							for(int i =0;i<3;i++) {
								for(int j =0;j<3;j++) {
									
									if(board[x-1+i][y-1+j]==null)complete=true;
								}
							}
							if(complete)continue;
							
							a.addScore(9);
							
							
						
					}
						
				}
			}
			
		}
	}

	/**
	 * Calculates points and adds them to the players score, if a feature was
	 * completed. FIELDS are only calculated when the game is over.
	 * 
	 * @param state The current game state.
	 */
	public void calculatePoints(State state) {
		// Fields are only calculated on final scoring.
		if (state == State.GAME_OVER)
			calculatePoints(FIELDS, state);

		calculatePoints(CASTLE, state);
		calculatePoints(ROAD, state);
		calculateMonasteries(state);
	}

		/**
	 * check if this feature is complete 
	 * @param type FeatureType
	 * @param x x-coordinate
	 * @param y y-coordinate
	 * @return true, if it is complete
	 */
	private boolean checkcomplete(FeatureType type,int x ,int y) {
		
		
	if(board[x][y]==null)return false;
	
	//if in this tile only one node belongs to this type,which means it is the end of feature
		int i =0;
		for(FeatureNode node :board[x][y].getNodes()) {
			if(node.getType()==type)i++;
			
		}
		if(i==1)return true ;
		
		//road can be complete if node in the middle of tile is also a road
				if(type==ROAD) {
					if(board[x][y].getNode(Position.CENTER).getType()==type)return true;
					
				}
		//if node is on top ,go check next tile above it
		if(board[x][y].getNode(TOP).getType()==type) {
			
			checkcomplete( type,x, y+1); 
		}
		
		
		if(board[x][y].getNode(BOTTOM).getType()==type) {
			
			checkcomplete( type,x, y-1); 
		}
		
		
		if(board[x][y].getNode(LEFT).getType()==type) {
			
			checkcomplete( type,x-1, y); 
		}
		
		
		
		if(board[x][y].getNode(RIGHT).getType()==type) {
			
			checkcomplete( type,x+1, y); 
		}
		return false;
	}

	/**
	 * find all node that connects to the node you give in
	 * @param type FeatureType
	 * @param x x-coordinate
	 * @param y y-coordinate
	 * @param score 
	 * @param besuchtenode node u have visited
	 * @param spielfigur a list used to save the player
	 */
	private void findallconnected(FeatureType type,int x ,int y,int score,List<Node<FeatureType>> besuchtenode,HashMap<Node<FeatureType>,Player> spielfigur ) {
		
		if(board[x][y]!=null) {
			
		int i =0;
		for(FeatureNode node :board[x][y].getNodes()) {
			if(node.getType()==type)i++;
			
		}
		//if in this tile only one node belongs to this type,which means it is the end of feature
		if(i>1) {
		
		if(board[x][y].getNode(TOP).getType()==type && !besuchtenode.contains(board[x][y].getNode(TOP))) {
			if(board[x][y].getNode(TOP).hasMeeple()) {
				spielfigur.put(board[x][y].getNode(TOP),board[x][y].getNode(TOP).getPlayer());
			}
			if(type==FeatureType.CASTLE)score=+2;//for CASTLE game-not-over
		    else if(type==FeatureType.ROAD) score++;//for ROAD game-over and game-not-over
		    else score++;//for Felder game-over
		    besuchtenode.add(board[x][y].getNode(TOP));
			findallconnected( type,x, y+1,score,besuchtenode,spielfigur); 
		}
		
		
		if(board[x][y].getNode(BOTTOM).getType()==type  && !besuchtenode.contains(board[x][y].getNode(TOP))) {
			if(board[x][y].getNode(BOTTOM).hasMeeple()) {				
				spielfigur.put(board[x][y].getNode(BOTTOM), board[x][y].getNode(BOTTOM).getPlayer());
			}
			if(type==FeatureType.CASTLE)score=+2;
			else if(type==FeatureType.ROAD) score++;
		    else score++;
		    
			besuchtenode.add(board[x][y].getNode(BOTTOM));
			findallconnected( type,x, y-1,score,besuchtenode,spielfigur); 
		}
		
		
		if(board[x][y].getNode(LEFT).getType()==type  && !besuchtenode.contains(board[x][y].getNode(TOP))) {
			if(board[x][y].getNode(LEFT).hasMeeple()) {
				
				spielfigur.put(board[x][y].getNode(LEFT),board[x][y].getNode(LEFT).getPlayer());
			}
			if(type==FeatureType.CASTLE)score=+2;
			else if(type==FeatureType.ROAD) score++;
		    else score++;
		   besuchtenode.add(board[x][y].getNode(LEFT));
			findallconnected( type,x-1, y,score,besuchtenode,spielfigur); 
		}
		
		
		
		if(board[x][y].getNode(RIGHT).getType()==type  && !besuchtenode.contains(board[x][y].getNode(TOP))) {
			if(board[x][y].getNode(RIGHT).hasMeeple()) {
				
				spielfigur.put( board[x][y].getNode(RIGHT),board[x][y].getNode(RIGHT).getPlayer());
			}
			if(type==FeatureType.CASTLE)score=+2;
		    if(type==FeatureType.ROAD) score++;
		    else if(type==FeatureType.ROAD) score++;
		    else score++;
		    besuchtenode.add(board[x][y].getNode(RIGHT));
			findallconnected( type,x+1, y,score,besuchtenode,spielfigur); 
		}
		}
		//when we come to the end of this FeatureType,we need to save it and add the score 
		else {
			if(board[x][y].getNode(TOP).getType()==type  && !besuchtenode.contains(board[x][y].getNode(TOP))) {
				if(board[x][y].getNode(TOP).hasMeeple()) {
					spielfigur.put( board[x][y].getNode(TOP),board[x][y].getNode(TOP).getPlayer());
				}
				if(type==FeatureType.CASTLE)score=+2;
			    if(type==FeatureType.ROAD) score++;
			   besuchtenode.add(board[x][y].getNode(TOP));
				
			}
			
			
			if(board[x][y].getNode(BOTTOM).getType()==type  && !besuchtenode.contains(board[x][y].getNode(TOP))) {
				if(board[x][y].getNode(BOTTOM).hasMeeple()) {				
					spielfigur.put(board[x][y].getNode(BOTTOM),board[x][y].getNode(BOTTOM).getPlayer());
				}
				if(type==FeatureType.CASTLE)score=+2;
			    else if(type==FeatureType.ROAD) score++;
			    else score++;
			    besuchtenode.add(board[x][y].getNode(BOTTOM));
				
			}
			
			
			if(board[x][y].getNode(LEFT).getType()==type  && !besuchtenode.contains(board[x][y].getNode(TOP))) {
				if(board[x][y].getNode(LEFT).hasMeeple()) {
					
					spielfigur.put(board[x][y].getNode(LEFT), board[x][y].getNode(LEFT).getPlayer());
				}
				if(type==FeatureType.CASTLE)score=+2;
				else if(type==FeatureType.ROAD) score++;
			    else score++;
			   besuchtenode.add(board[x][y].getNode(LEFT));
				
			}
			
			
			
			if(board[x][y].getNode(RIGHT).getType()==type  && !besuchtenode.contains(board[x][y].getNode(TOP))) {
				if(board[x][y].getNode(RIGHT).hasMeeple()) {
					
					spielfigur.put( board[x][y].getNode(RIGHT),board[x][y].getNode(RIGHT).getPlayer());
				}
				if(type==FeatureType.CASTLE)score=+2;
			    else if(type==FeatureType.ROAD) score++;
			    else score++;
			    besuchtenode.add(board[x][y].getNode(RIGHT));
				
		}
			
		}
		}
	}
	/**
	 * Calculates and adds points to the players that scored a feature. If the given
	 * state is GAME_OVER, points are added to the player with the most meeple on a
	 * subgraph, even if it is not completed.
	 * 
	 * @param type  The FeatureType that is supposed to be calculated.
	 * @param state The current game state.
	 */
	public void calculatePoints(FeatureType type, State state) {
		List<Node<FeatureType>> nodeList = new ArrayList<>(graph.getNodes(type));
		List<Node<FeatureType>> besuchtenode = new ArrayList<Node<FeatureType>>();
		
		
		
		// queue defines the connected graph. If this queue is empty, every node in this graph will be visited.
		// if nodeList is non-empty, insert the next node of nodeList into this queue
		ArrayDeque<Node<FeatureType>> queue = new ArrayDeque<>();
		


		int score = 0;
		//boolean completed = true; // Is the feature completed? Is set to false if a node is visited that does not
									// connect to any other tile
	

		queue.push(nodeList.remove(0));
		besuchtenode.add(queue.getLast());
		// Iterate as long as the queue is not empty
		// Remember: queue defines a connected graph
		
		//TODO
		if(state != State.GAME_OVER) {
			if(type ==FeatureType.CASTLE) {
				HashMap<Node<FeatureType>,Player>  figur =new HashMap<Node<FeatureType>,Player> ();
				//wont end till nodelist is empty
					while(nodeList.size()!=0) {
						//find the tile which has this node
					for(int x =0;x<board.length;x++) {
						for(int y=0;y<board[0].length;y++) {
							//to save the mepple
							HashMap<Node<FeatureType>,Player>  spielfigur =new HashMap<Node<FeatureType>,Player> ();
							
						
							
                           //if board have this node
									if(board[x][y].containsNode((FeatureNode)queue.getLast())){
										//start from the next tile 
										int x1=0 , y1=0;
										if(board[x][y].getNode(TOP)==queue.getLast()) {
											x1=x; y1=y+1;
										}
										if(board[x][y].getNode(BOTTOM)==queue.getLast()) {
											x1=x; y1=y-1;
										}
										if(board[x][y].getNode(LEFT)==queue.getLast()) {
											x1=x-1; y1=y;
										}
										if(board[x][y].getNode(RIGHT)==queue.getLast()) {
											x1=x+1; y1=y;
										}
										if(((FeatureNode) queue.getLast()).hasMeeple()) {
											
											spielfigur.put((FeatureNode)queue.getLast(),((FeatureNode) queue.getLast()).getPlayer());
										}
										
										
										
										//check if  feature is complete
										if(checkcomplete( type, x1 , y1)) {
											//find all feature that  connects to this node
											findallconnected( type, x1 , y1, score, besuchtenode, spielfigur );
											figur.putAll(spielfigur);									
											besuchtenode.remove(queue.getLast());
										for(Node<FeatureType> n:besuchtenode) {
											nodeList.remove(n);
										}
										}
										if(nodeList.get(0)!=null)queue.push(nodeList.remove(0));
										
										
					}
					
				}
					}
					if(figur!=null) {
						Collection<Player> playerCollection =figur.values();
						for(Player p : playerCollection) {
							p.addScore(2);
							
							}
						Set<Player> set = new HashSet<>(playerCollection);
						for(Player p : set) {
							p.addScore(score);
							
							}
					
					}
				
					
				
				
			}
			
			
			if(type ==FeatureType.ROAD) {
				HashMap<Node<FeatureType>,Player> figurRoad =new HashMap<Node<FeatureType>,Player>();
					while(nodeList.size()!=0) {
						
					for(int x =0;x<board.length;x++) {
						for(int y=0;y<board[0].length;y++) {
							HashMap<Node<FeatureType>,Player> spielfigur =new HashMap<Node<FeatureType>,Player>();
							
							
									if(board[x][y].containsNode((FeatureNode)queue.getLast())){
										int x1=0 , y1=0;
										if(board[x][y].getNode(TOP)==queue.getLast()) {
											x1=x; y1=y+1;
										}
										if(board[x][y].getNode(BOTTOM)==queue.getLast()) {
											x1=x; y1=y-1;
										}
										if(board[x][y].getNode(LEFT)==queue.getLast()) {
											x1=x-1; y1=y;
										}
										if(board[x][y].getNode(RIGHT)==queue.getLast()) {
											x1=x+1; y1=y;
										}
                                        if(((FeatureNode) queue.getLast()).hasMeeple()) {
											
											spielfigur.put((FeatureNode)queue.getLast(),((FeatureNode) queue.getLast()).getPlayer());
										}
										
										if(checkcomplete( type, x1 , y1)) {
											findallconnected( type, x1 , y1, score, besuchtenode, spielfigur );
											figurRoad.putAll(spielfigur);
											besuchtenode.remove(queue.getLast());
										
										for(Node<FeatureType> n:besuchtenode) {
											nodeList.remove(n);
										}
										
										
										
										}
										if(nodeList.get(0)!=null)queue.push(nodeList.remove(0));
									}
									
										
					}
					
				}
					}
				if(figurRoad!=null) {
					Collection<Player> playerCollection =figurRoad.values();
					Set<Player> set = new HashSet<>(playerCollection);
					for(Player p : set) {
						p.addScore(score);
						
						}
				}
					
					
				
				
				
			}
			
		}
		
		// Hint:
		// If there is one straight positioned node that does not connect to another
		// tile, the feature cannot be completed.


		//TODO
		if(state == State.GAME_OVER) {
			if(type==CASTLE) {
						
				while(nodeList.size()!=0) {
					HashMap<Node<FeatureType>,Player>  figuroverStadt =new HashMap<Node<FeatureType>,Player> ();
					
				for(int x =0;x<board.length;x++) {
					for(int y=0;y<board[0].length;y++) {
						HashMap<Node<FeatureType>,Player>  spielfigur =new HashMap<Node<FeatureType>,Player>();
						
						
								if(board[x][y].containsNode((FeatureNode)queue.getLast())){
									int x1=0 , y1=0;
									
									if(board[x][y].getNode(TOP)==queue.getLast() ) {
										x1=x; y1=y+1;
										
									}
									if(board[x][y].getNode(BOTTOM)==queue.getLast()) {
										x1=x; y1=y-1;
										
									}
									if(board[x][y].getNode(LEFT)==queue.getLast()) {
										x1=x-1; y1=y;
										

									}
									if(board[x][y].getNode(RIGHT)==queue.getLast()) {
										x1=x+1; y1=y;
										
									}
									if(((FeatureNode) queue.getLast()).hasMeeple()) {
										
										spielfigur.put((FeatureNode)queue.getLast(),((FeatureNode) queue.getLast()).getPlayer());
									}
									
									if(!checkcomplete( type, x1 , y1) ) {
										
										findallconnected( type, x1 , y1, score, besuchtenode, spielfigur );
										figuroverStadt.putAll(spielfigur);
										besuchtenode.remove(queue.getLast());
										for(Node<FeatureType> n:besuchtenode) {
											nodeList.remove(n);
										}
										
									}
									if(nodeList.get(0)!=null)queue.push(nodeList.remove(0));
										
									

										
								}
				
			}
				}
				if(figuroverStadt!=null) {
				Collection<Player> playerCollection =figuroverStadt.values();
				for(Player p : playerCollection) {
					p.addScore(1);
					
					}
				Set<Player> set = new HashSet<>(playerCollection);
				for(Player p : set) {
					p.addScore(score);
					
					}
				}
				
				
				}
			}
			
			if(type==ROAD) {
				
				while(nodeList.size()!=0) {
					HashMap<Node<FeatureType>,Player>  figuroverroad =new HashMap<Node<FeatureType>,Player> ();
					
				for(int x =0;x<board.length;x++) {
					for(int y=0;y<board[0].length;y++) {
						HashMap<Node<FeatureType>,Player>  spielfigur =new HashMap<Node<FeatureType>,Player> ();
						
						
								if(board[x][y].containsNode((FeatureNode)queue.getLast())){
									int x1=0 , y1=0;
									
									if(board[x][y].getNode(TOP)==queue.getLast() ) {
										x1=x; y1=y+1;
										
									}
									if(board[x][y].getNode(BOTTOM)==queue.getLast()) {
										x1=x; y1=y-1;
										
									}
									if(board[x][y].getNode(LEFT)==queue.getLast()) {
										x1=x-1; y1=y;
										

									}
									if(board[x][y].getNode(RIGHT)==queue.getLast()) {
										x1=x+1; y1=y;
										
									}
									if(((FeatureNode) queue.getLast()).hasMeeple()) {
										
										spielfigur.put((FeatureNode)queue.getLast(),((FeatureNode) queue.getLast()).getPlayer());
									}
									
									if(!checkcomplete( type, x1 , y1) ) {
										
										findallconnected( type, x1 , y1, score, besuchtenode, spielfigur );
										figuroverroad.putAll(spielfigur);
										besuchtenode.remove(queue.getLast());
										for(Node<FeatureType> n:besuchtenode) {
											nodeList.remove(n);
										}
										
									}
									if(nodeList.get(0)!=null)queue.push(nodeList.remove(0));
										
									

										
								}
				
			}
				}
				if(figuroverroad!=null) {
				Collection<Player> playerCollection =figuroverroad.values();
				Set<Player> set = new HashSet<>(playerCollection);
				for(Player p : set) {
					p.addScore(score);
					
					}
				}
				
				
				}
			}
			
          if(type==FIELDS) {
				
				while(nodeList.size()!=0) {
					HashMap<Node<FeatureType>,Player>  figuroverroad =new HashMap<Node<FeatureType>,Player>();
					
				for(int x =0;x<board.length;x++) {
					for(int y=0;y<board[0].length;y++) {
						HashMap<Node<FeatureType>,Player>  spielfigur =new HashMap<Node<FeatureType>,Player>();
						
						
								if(board[x][y].containsNode((FeatureNode)queue.getLast())){
									int x1=0 , y1=0;
									
									if(board[x][y].getNode(TOP)==queue.getLast() ) {
										x1=x; y1=y+1;
										
									}
									if(board[x][y].getNode(BOTTOM)==queue.getLast()) {
										x1=x; y1=y-1;
										
									}
									if(board[x][y].getNode(LEFT)==queue.getLast()) {
										x1=x-1; y1=y;
										

									}
									if(board[x][y].getNode(RIGHT)==queue.getLast()) {
										x1=x+1; y1=y;
										
									}
									if(((FeatureNode) queue.getLast()).hasMeeple()) {
										
										spielfigur.put((FeatureNode)queue.getLast(),((FeatureNode) queue.getLast()).getPlayer());
									}
									
									if(!checkcomplete( type, x1 , y1) ) {
										
										findallconnected( type, x1 , y1, score, besuchtenode, spielfigur );
										figuroverroad.putAll(spielfigur);
										besuchtenode.remove(queue.getLast());
										for(Node<FeatureType> n:besuchtenode) {
											nodeList.remove(n);
										}
										
									}
									if(nodeList.get(0)!=null)queue.push(nodeList.remove(0));
										
									

										
								}
				
			}
				}
				if(figuroverroad!=null) {
				Collection<Player> playerCollection =figuroverroad.values();
				Set<Player> set = new HashSet<>(playerCollection);
				for(Player p : set) {
					p.addScore((int)score/4);
					
					}
				}
				
				}
			}
			
				
				
			
			
		}
		}
			}
	/**
	 * Returns all Tiles on the Gameboard.
	 * 
	 * @return all Tiles on the Gameboard.
	 */
	public List<Tile> getTiles() {
		return tiles;
	}

	/**
	 * Returns the Tile containing the given FeatureNode.
	 * 
	 * @param node A FeatureNode.
	 * @return the Tile containing the given FeatureNode.
	 */
	private Tile getTileContainingNode(FeatureNode node) {
		for (Tile t : tiles) {
			if (t.containsNode(node))
				return t;
		}
		return null;
	}

	/**
	 * Returns the spots on the most recently placed tile on which it is allowed to
	 * place a meeple .
	 * 
	 * @return The spots on which it is allowed to place a meeple as a boolean array
	 *         representing the tile split in nine cells from top left, to right, to
	 *         bottom right. If there is no spot available at all, returns null.
	 */
	public boolean[] getMeepleSpots() {
		boolean[] positions = new boolean[9];
		boolean anySpot = false; // if there is not a single spot, this remains false

		for (Position p : Position.values()) {
			FeatureNode n = newestTile.getNodeAtPosition(p);
			if (n != null)
				if (n.hasMeepleSpot() && !hasMeepleOnSubGraph(n))
					positions[p.ordinal()] = anySpot = true;
		}

		if (anySpot)
			return positions;
		else
			return null;
	}

	/**
	 * Checks if there are any meeple on the subgraph that FeatureNode n is a part
	 * of.
	 * 
	 * @param n The FeatureNode to be checked.
	 * @return True if the given FeatureNode has any meeple on its subgraph, false
	 *         if not.
	 */
	private boolean hasMeepleOnSubGraph(FeatureNode n) {
		List<Node<FeatureType>> visitedNodes = new ArrayList<>();
		ArrayDeque<Node<FeatureType>> queue = new ArrayDeque<>();

		queue.push(n);
		while (!queue.isEmpty()) {
			FeatureNode node = (FeatureNode) queue.pop();
			if (node.hasMeeple())
				return true;

			List<Edge<FeatureType>> edges = graph.getEdges(node);
			for (Edge<FeatureType> edge : edges) {
				Node<FeatureType> nextNode = edge.getOtherNode(node);
				if (!visitedNodes.contains(nextNode)) {
					queue.push(nextNode);
					visitedNodes.add(nextNode);
				}
			}
		}
		return false;
	}

	/**
	 * Returns the newest tile.
	 * 
	 * @return the newest tile.
	 */
	public Tile getNewestTile() {
		return newestTile;
	}

	/**
	 * Places a meeple of given player at given position on the most recently placed
	 * tile (it is only allowed to place meeple on the most recent tile).
	 * 
	 * @param position The position the meeple is supposed to be placed on on the
	 *                 tile (separated in a 3x3 grid).
	 * @param player   The owner of the meeple.
	 */
	public void placeMeeple(Position position, Player player) {
		board[newestTile.x][newestTile.y].getNode(position).setPlayer(player);
		player.removeMeeple();
	}

	public Tile[][] getBoard() {
		return board;
	}

	public FeatureGraph getGraph() {
		return this.graph;
	}

	public void setFeatureGraph(FeatureGraph graph) {
		this.graph = graph;
	}
}
