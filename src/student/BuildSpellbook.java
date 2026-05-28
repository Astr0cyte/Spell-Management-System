package student;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class BuildSpellbook {

	public final Integer MAXCOMS = 1000;

	// For tracking physical graph structure.
	public Vector<LinkedList<Edge>> adjList;

	// For tracking state.
	public Vector<String> spellNames; // List of corresponding spell name.
	public Vector<Integer> learnedSpellLevels; // List of corresponding spell level.
	public Vector<Boolean> explicitlyLearned;	// Overall: it is a list of marks for spells that can be deleted.
	public Vector<Integer> learnOrder; // For ENUM.

	public BuildSpellbook() {
		this.adjList = new Vector<>();
		this.spellNames = new Vector<>();
		this.learnedSpellLevels = new Vector<>();
		this.explicitlyLearned = new Vector<>();
		this.learnOrder = new Vector<>();
	}

	// <------------------------------------------->
	// START helper methods for adjacencyList
	public void addVertex(String spellName) {
    
		if (this.spellNames.contains(spellName)) { 
			return; // Already exists, drop out
		}

		// Synchronise vectors.
		this.spellNames.add(spellName);
		this.adjList.add(new LinkedList<>());
		this.learnedSpellLevels.add(0); // 0 indicates level 0 = unlearned.
		this.explicitlyLearned.add(false);
	}

	public void addEdge(int source, int destination, int requiredLevel, int insertionId) {
		Edge newConnection = new Edge(destination, requiredLevel, insertionId);
		this.adjList.get(source).add(newConnection); // Graph structure remains numeric.
	}

	public int getSpellIndex(String spellName) {
		return this.spellNames.indexOf(spellName);
	}
	// END helper methods for adjacencyList
	//  <------------------------------------------->

	// Helper method for creating an edge.
	public void registerPrereq(String[] parts, int insertionId){
		// Add vertices first (iterates from highest level spell to lowest).
		for(int j = 1; j < parts.length; j++) {
			if (Character.isLetter(parts[j].charAt(0))) {
				this.addVertex(parts[j]);
			}
		}

		int focalSpell = this.getSpellIndex(parts[1]); // More advanced spell in the relationship.

			// Creating a "fan out" structure. Connect each subsequent spell to the focal spell.
			for(int k = 2; k < parts.length; k++) {
				if(Character.isLetter(parts[k].charAt(0))) {
					int requiredLevel = 1; // Default level.

					// "If k is not out of bounds && the next character is a number".
					if ((k + 1) < parts.length && Character.isDigit(parts[k + 1].charAt(0))) {
						requiredLevel = Integer.parseInt(parts[k + 1]); // Overwrite default level.
						k++; // Advance the pointer so the loop skips this number token next turn.
					}
					
					// Get destination edge ID.
					int edgeDest = this.getSpellIndex(parts[k]);
					this.addEdge(focalSpell, edgeDest, requiredLevel, insertionId); // Connect focalSpell -> prereqSpell.
				}
			}
	}

	// Helper method for identifying dependencies & their required levels.
	private int getRequiredFloor(int targetSpellIdx, boolean ignoreExplicit) {
		// Implicit = 0.
		int highestFloor = 0;

		// Explicit = 1.
		if(!ignoreExplicit && this.explicitlyLearned.get(targetSpellIdx)) {
			highestFloor = 1;
		}

		for(int i = 0; i < adjList.size(); i++) {

			int checkIdxLevel = this.learnedSpellLevels.get(i);

			// Extra guard: execute a search through LinkedLists ONLY for spells that: 
				// 1) ARENT the target spell, AND 2) are > level 0.
			if((i != targetSpellIdx) && (checkIdxLevel > 0)) {
				for(int k = 0; k < adjList.get(i).size(); k++) {
					Edge edge = this.adjList.get(i).get(k);

					// If the target spell is linked to the searched up spell...
					if(edge.targetIdx == targetSpellIdx) {
						// ... check if the required level for that searched spell exceeds the current floor. If so, update currentFloor to that value.
						if(edge.requiredLevel > highestFloor) {
							highestFloor = edge.requiredLevel;
						}
					}
				}
			} 
		} // By the end of this loop highestFloor is the maximum required level it should be.
		return highestFloor;
	}

	public void recurseLearnSpell(Vector<String> echo, int spellIdx, int targetLevel) {
		// Base case - guarding against possible subsequent learned spells && If the current level is already high enough to satisfy the target level needed.
		if(this.learnedSpellLevels.get(spellIdx) >= targetLevel) {
			return;
		}

		// Retrieve the head of the specific spell we want to LEARN.
		LinkedList<Edge> prerequisites = this.adjList.get(spellIdx);

		// Check for spells that may need to be learnt before desired spell.
		for(int i = 0; i < prerequisites.size(); i++) {
			int prereqSpellIdx = prerequisites.get(i).targetIdx;
			int prereqRequiredLevel = prerequisites.get(i).requiredLevel; // Retrieve the requisite level.
			
			recurseLearnSpell(echo, prereqSpellIdx, prereqRequiredLevel);
		}

		// Levelling up logic.
		int startingLevel = this.learnedSpellLevels.get(spellIdx);
		int levelDelta = targetLevel - startingLevel; // Needed for the echo: displays how many levels have been added ON TOP OF current level.

		this.learnedSpellLevels.set(spellIdx, targetLevel);
		
		// Guard against adding duplicates + chronological levelup tracking.
		if(!this.learnOrder.contains(spellIdx)) {
			this.learnOrder.add(spellIdx);
		}

		/* 2 Echo message cases:
			1 - The spell is being learned for the first time at level 1.
			2 - The spell has already been learned and is getting a LEVEL UP to a target level.
		*/ 
		if (startingLevel == 0 && targetLevel == 1) {
			// Case 1.
    		String learntMsg = "    Learning " + this.spellNames.get(spellIdx) + " level 1";
    		echo.add(learntMsg);
		} else {
    		// Case 2: Every other jump (0 -> 4 yielding +4, or 1 -> 4 yielding +3).
   			String learntMsg = "    Learning " + this.spellNames.get(spellIdx) + " level +" + levelDelta;
    		echo.add(learntMsg);
		}
	}

	public void recurseUnlearnSpell(Vector<String> echo, int spellIdx) {
		if (this.explicitlyLearned.get(spellIdx)) {
    		return;
		}
		
		int requiredFloor = this.getRequiredFloor(spellIdx, false);
		int currentLevel = this.learnedSpellLevels.get(spellIdx);
		
		// Base case: If we are already at or below the required floor, stop unlearning!
		if (currentLevel <= requiredFloor) {
    		return;
		}

		this.learnedSpellLevels.set(spellIdx, requiredFloor);

		// Case 1: floor = 0, indicating implicit or marked for removal.
		if(requiredFloor == 0) {
			this.learnOrder.remove(Integer.valueOf(spellIdx));
			String forgetMsg = "    Forgetting "+this.spellNames.get(spellIdx);
			echo.add(forgetMsg);
		}

		LinkedList<Edge> deletionCandidate = this.adjList.get(spellIdx);

		for(int i = deletionCandidate.size() - 1; i >= 0; i--) {
			int deletionSpellIdx = deletionCandidate.get(i).targetIdx;
			recurseUnlearnSpell(echo, deletionSpellIdx);
		}
	}

	// <--------------------------------->
	// Methods for C level tasks.
	public boolean hasCycle(int startSpellIdx) {
		
		Set<Integer> visited = new HashSet<>(); // Sets are the ultimate tool for this job because they physcially cannot contain duplicate values!
    	Set<Integer> currentPath = new HashSet<>();
    
    	return dfsDetectCycle(startSpellIdx, visited, currentPath);
	}

	public boolean dfsDetectCycle(int currentIdx, Set<Integer> visited, Set<Integer> currentPath) {
		
		visited.add(currentIdx);
		currentPath.add(currentIdx);
		LinkedList<Edge> connectedNodes = this.adjList.get(currentIdx); // Retrieve list of edges for current spell.
		
		for(int i = 0; i < connectedNodes.size(); i++) {
			
			int neighbourIdx = connectedNodes.get(i).targetIdx;

			if(!visited.contains(neighbourIdx)) {
				if(dfsDetectCycle(neighbourIdx, visited, currentPath)) {
					return true; // Recursively explore new unvisited edges.
				}
			} else if (currentPath.contains(neighbourIdx)) {
				return true; // If we get to the else block -> node is in visited list. Then, if node is in the current path list, we have a cycle. Return true.
			}
		}

		currentPath.remove(currentIdx);
		return false; // Dropped out of loop = no cycles found.
	}

    /*
	* DEPRECATED HELPER.
	*
	* This was part of the earlier focal-spell cycle search approach.
	* Final D/HD logic now uses findAllCyclesInGraph() instead.
	*
	public List<List<Integer>> findAllCyclesFromStartingSpell(int startSpellIdx) {
		Set<Integer> visited = new HashSet<>();
		LinkedHashSet<Integer> currentPath = new LinkedHashSet<>();
		List<List<Integer>> cycleList = new ArrayList<>();

		dfsMapCycles(startSpellIdx, visited, currentPath, cycleList);

		return cycleList;
	}
	*/

	// <-------------------------------->
	// Methods for D/HD level tasks.

	public List<List<Integer>> findAllCyclesInGraph() {
		List<List<Integer>> cycleList = new ArrayList<>();

		for (int i = 0; i < this.adjList.size(); i++) {
			Set<Integer> visited = new HashSet<>();
			LinkedHashSet<Integer> currentPath = new LinkedHashSet<>();

			dfsMapCycles(i, visited, currentPath, cycleList);
		}

		return cycleList;
	}

	public void dfsMapCycles(int currentIdx, Set<Integer> visited, LinkedHashSet<Integer> currentPath, List<List<Integer>> cycleList) {
		
		visited.add(currentIdx);
		currentPath.add(currentIdx);
		LinkedList<Edge> connectedNodes = this.adjList.get(currentIdx); // Retrieve list of edges for current spell.

		for(int i = 0; i < connectedNodes.size(); i++) {
			
			int neighbourIdx = connectedNodes.get(i).targetIdx;

			if(!visited.contains(neighbourIdx)) {
				dfsMapCycles(neighbourIdx, visited, currentPath, cycleList);
			} else if (currentPath.contains(neighbourIdx)) {
				// If we get to this point, a cycle exists.
				List<Integer> newCycle = new ArrayList<>();
				boolean recording = false;
				// Fill list entry with cycle.
				for (Integer step : currentPath) {
    				if (step == neighbourIdx) {
        				recording = true; // Start of cycle has been found.
   					}

    				if (recording) {
        				newCycle.add(step);
    				}
				}
				// Add filled list entry to master list.
				cycleList.add(newCycle);
			}
		}
		currentPath.remove(currentIdx);
	}

	private CycleRecommend getLargestCycleRecommendInGraph() {
		List<List<Integer>> allCycles = findAllCyclesInGraph();

		if (allCycles.isEmpty()) {
			return null;
		}

		List<Integer> targetCycle = allCycles.get(0);

		for (int j = 0; j < allCycles.size(); j++) {
			if (allCycles.get(j).size() > targetCycle.size()) {
				targetCycle = allCycles.get(j);
			}
		}

		int highestInsertionId = -1;
		int sourceSpellIdx = -1;
		int prereqSpellIdx = -1;

		for (int k = 0; k < targetCycle.size(); k++) {
			int sourceSpell = targetCycle.get(k);
			int destSpell;

			if (k == targetCycle.size() - 1) {
				destSpell = targetCycle.get(0);
			} else {
				destSpell = targetCycle.get(k + 1);
			}

			LinkedList<Edge> edges = this.adjList.get(sourceSpell);

			for (int e = 0; e < edges.size(); e++) {
				Edge currentEdge = edges.get(e);

				if (currentEdge.targetIdx == destSpell) {
					if (currentEdge.insertionId > highestInsertionId) {
						highestInsertionId = currentEdge.insertionId;
						sourceSpellIdx = sourceSpell;
						prereqSpellIdx = destSpell;
					}

					break;
				}
			}
		}

		return new CycleRecommend(sourceSpellIdx, prereqSpellIdx);
	}

	public CycleRecommend getSmallestCycleRecommendInGraph() {
		List<List<Integer>> allCycles = findAllCyclesInGraph();

		if (allCycles.isEmpty()) {
			return null;
		}

		List<Integer> targetCycle = allCycles.get(0);

		for (int j = 0; j < allCycles.size(); j++) {
			// Largest/smallest comparison.
			if (allCycles.get(j).size() < targetCycle.size()) {
				targetCycle = allCycles.get(j);
			}
		}

		int highestInsertionId = -1;
		int sourceSpellIdx = -1;
		int prereqSpellIdx = -1;

		for (int k = 0; k < targetCycle.size(); k++) {
			int sourceSpell = targetCycle.get(k);
			int destSpell;

			if (k == targetCycle.size() - 1) {
				destSpell = targetCycle.get(0);
			} else {
				destSpell = targetCycle.get(k + 1);
			}

			LinkedList<Edge> edges = this.adjList.get(sourceSpell);

			for (int e = 0; e < edges.size(); e++) {
				Edge currentEdge = edges.get(e);

				if (currentEdge.targetIdx == destSpell) {
					if (currentEdge.insertionId > highestInsertionId) {
						highestInsertionId = currentEdge.insertionId;
						sourceSpellIdx = sourceSpell;
						prereqSpellIdx = destSpell;
					}

					break;
				}
			}
		}

		return new CycleRecommend(sourceSpellIdx, prereqSpellIdx);
	}


	// <----------------------------------------------------------->
	// Overall "switchboard" in which all P/C/D/HD methods call to.
	public Vector<String> arcaneCommandCentre(Vector<String> specs, Integer N, int operationMode) {
		
		Vector<String> echo = new Vector<String>();
		int iterationLength = 0;

		if(specs.size() < N) {
			iterationLength = specs.size(); // File is shorter than N. Cap at file size.
		} else {
			iterationLength = N; // File is longer than N. Cap at N.
		}

		// Diagnostic mode.
		boolean diagnosticMode = false;

		for(int i = 0; i < iterationLength; i++) {
			
			String line = specs.get(i);
			String[] parts = line.split(" ");
			
			if(parts[0].equals("END")) {
				echo.add(line);
				return echo;
			}

			// Diagnostic mode = add all lines to echo, but only operate on them if they are PREREQ.
			if (diagnosticMode) {
				echo.add(line);

				if (parts[0].equals("PREREQ")) {
					registerPrereq(parts, i);

					if (operationMode == 1) {
						echo.add("    Found cycle in prereqs");
					} else if (operationMode == 2) {
						CycleRecommend rec = getLargestCycleRecommendInGraph();
						echo.add("    Found cycle in prereqs");

						if (rec != null) {
							echo.add("    Suggest forgetting PREREQ "+ this.spellNames.get(rec.sourceIdx) + " "+ this.spellNames.get(rec.destIdx));
						}
					} else if (operationMode == 3) {
						CycleRecommend rec = getSmallestCycleRecommendInGraph();
						echo.add("    Found cycle in prereqs");

						if (rec != null) {
							echo.add("    Suggest forgetting PREREQ "+ this.spellNames.get(rec.sourceIdx) + " "+ this.spellNames.get(rec.destIdx));
						}
					}
				}
				continue; // Skip regular operation below. Move onto next line.
			}

			switch(parts[0]) {
				case "PREREQ": 
					echo.add(line);

					registerPrereq(parts, i);
					int focalSpell = this.getSpellIndex(parts[1]);

						if(operationMode == 1) {
							// DFS for cycle detection.
							if(hasCycle(focalSpell)) {
								echo.add("    Found cycle in prereqs");
								diagnosticMode = true;
							}
						} else if (operationMode == 2) {
							CycleRecommend rec = getLargestCycleRecommendInGraph();

							if (rec != null) {
								echo.add("    Found cycle in prereqs");
								echo.add("    Suggest forgetting PREREQ "+ this.spellNames.get(rec.sourceIdx) + " "+ this.spellNames.get(rec.destIdx));
								diagnosticMode = true;
							}
						} else if (operationMode == 3) {
							CycleRecommend rec = getSmallestCycleRecommendInGraph();

							if (rec != null) {
								echo.add("    Found cycle in prereqs");
								echo.add("    Suggest forgetting PREREQ "+ this.spellNames.get(rec.sourceIdx) + " "+ this.spellNames.get(rec.destIdx));

								diagnosticMode = true;
							}
						}
				break;
				
				case "LEARN": 
					echo.add(line);
					
					// The spell to be learnt will ALWAYS be the second entry in 'parts' - by definition of how LEARN works.
					this.addVertex(parts[1]);
					int currentSpellLrn = this.getSpellIndex(parts[1]);

					if(this.learnedSpellLevels.get(currentSpellLrn) > 0) {
						int nextTargetLevel = this.learnedSpellLevels.get(currentSpellLrn) + 1;
        				recurseLearnSpell(echo, currentSpellLrn, nextTargetLevel);
					} else {
						this.explicitlyLearned.set(currentSpellLrn, true);
						recurseLearnSpell(echo, currentSpellLrn, 1);
					}
				break;

				case "FORGET":
					echo.add(line);

					int currentSpellFrgt = this.getSpellIndex(parts[1]);
					int currentLevel = this.learnedSpellLevels.get(currentSpellFrgt);

					// Case 1: The spell has not been learned at all
					if (currentLevel == 0) {
						echo.add("    " + parts[1] + " is not learned");
					} 
					else {
						// Scan graph for other spell dependencies. Adapt to their level requirements.
						int graphDependencyFloor = this.getRequiredFloor(currentSpellFrgt, true);

						// Case 2: An external spell dependency is relying on the current spell being this level.
						if (currentLevel <= graphDependencyFloor) {
							echo.add("    " + parts[1] + " is still needed");
						} 
						// Case 3: Graph is clear! Safe to drop explicitlyLearned "shield" and delete.
						else {
							this.explicitlyLearned.set(currentSpellFrgt, false);
							recurseUnlearnSpell(echo, currentSpellFrgt); // Recursive unlearn call.
						}
					}
				break;
	
				case "ENUM":
					echo.add(line);

					for(int j = 0; j < this.learnOrder.size(); j++) {
						int currIdx = this.learnOrder.get(j);
						String currLearntSpell = this.spellNames.get(currIdx);
						int currLearntSpellLvl = this.learnedSpellLevels.get(currIdx);
						echo.add("    "+currLearntSpell+" level "+currLearntSpellLvl); 
					}

				break;
			}
		}
		return echo;
	}

	//PASS LEVEL
	public Vector<String> executeNSpecs (Vector<String> specs, Integer N) {
		// PRE: specs contains set of specifications read in by readSpecsFromFile()
		// POST: executed min(N, all) specifications,
        //       returning required output, one line per string in vector

		// Operation mode 0 = pass level.
		return arcaneCommandCentre(specs, N, 0);
	}
	
	// CREDIT LEVEL
	public Vector<String> executeNSpecswCheck (Vector<String> specs, Integer N) {
		// PRE: specs contains set of specifications read in by readSpecsFromFile()
		// POST: executed min(N, all) specifications, checking for cycles,
        //       returning required output, one line per string in vector

		return arcaneCommandCentre(specs, N, 1);
	}
	
	// D/HD LEVEL
	public Vector<String> executeNSpecswCheckRecLarge (Vector<String> specs, Integer N) {
		// PRE: specs contains set of specifications read in by readSpecsFromFile()
		// POST: executed min(N, all) specifications, checking for cycles and 
		//       recommending fix by removing largest cycle,
		//       returning required output, one line per string in vector

		return arcaneCommandCentre(specs, N, 2);
	}

	// D/HD LEVEL
	public Vector<String> executeNSpecswCheckRecSmall (Vector<String> specs, Integer N) {
		// PRE: specs contains set of specifications read in by readSpecsFromFile()
		// POST: executed min(N, all) specifications, checking for cycles and 
		//       recommending fix by removing smallest cycle,
        //       returning required output, one line per string in vector

		return arcaneCommandCentre(specs, N, 3);
	}

	// <-------------------->
	// Provided files below.
	public Vector<String> readSpecsFromFile(String fInName) throws IOException {
		// PRE: -
		// POST: returns lines from input file as vector of string

		BufferedReader fIn = new BufferedReader(
							 new FileReader(fInName));
		String s;
		Vector<String> comList = new Vector<String>();
		
		while ((s = fIn.readLine()) != null) {
			comList.add(s);
		}
		fIn.close();
		
		return comList;
	}

	public Vector<String> readSolnFromFile(String fInName, Integer N) throws IOException {
		// PRE: -
		// POST: returns (up to) N lines from input file as a vector of N strings;
		//       only the specification lines are counted in this N, not responses

		BufferedReader fIn = new BufferedReader(
							 new FileReader(fInName));
		String s;
		Vector<String> out = new Vector<String>();
		Integer i = 0;

		while (((s = fIn.readLine()) != null) && (i <= N)) {
			if ((i != N) || s.startsWith("   ")) // responses to commands start with three spaces
				out.add(s);
			if (!s.startsWith("   "))  
				i += 1;
		}
		fIn.close();
		
		return out;
	}
	
	public Boolean compareExecutedWSoln (Vector<String> execd, Vector<String> soln) {
		// PRE: -
		// POST: Returns True if execd and soln string-match exactly, False otherwise

		if (execd.size() != soln.size()) {
			return Boolean.FALSE;
		}
		for (int i = 0; i < execd.size(); i++) {
			if (!execd.get(i).equals(soln.get(i))) {
				return Boolean.FALSE;
			}
		}

		return Boolean.TRUE;

	}
	// End provided files.
	// <------------------>

	public static void main(String[] args) {
		
	}

	// <---------------------------------------------->
	// Edge has been updated to track insertion order.
	private class Edge {
		int targetIdx;
		int requiredLevel;
		int insertionId; // H/HD requirement - A tracker of spell chronology.

		private Edge(int targetIdx, int requiredLevel, int insertionId) {
			this.targetIdx = targetIdx;
			this.requiredLevel = requiredLevel;
			this.insertionId = insertionId;
		}
	}

	private class CycleRecommend {
		int sourceIdx;
		int destIdx;

		private CycleRecommend(int sourceIdx, int destIdx) {
			this.sourceIdx = sourceIdx;
			this.destIdx = destIdx;
		}
	}
}
