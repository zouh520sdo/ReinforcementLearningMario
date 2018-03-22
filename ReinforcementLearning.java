package gameai_huang.RL.ReinforcementLearningMario;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Random;

import ch.idsia.benchmark.mario.environments.Environment;

public class ReinforcementLearning {
	protected Random r;
	protected HashMap<Integer, double[]> hashQ;
	protected int numberOfActions;
	final double alpha = 0.1;
    final double gamma = 0.9;
    public double e; // for SARSA
    boolean isSARSA;
    
    // Data storing
    protected int currentState;
    protected int currentAction;
    protected int nextState;
    protected int nextAction;
    
	public ReinforcementLearning (int numberOfA, boolean SARSA)
	{
		r = new Random(System.currentTimeMillis());
		numberOfActions = numberOfA;
		hashQ = new HashMap<Integer, double[]>();
		e = 0.5;
		isSARSA = SARSA;
	}
	
	public ReinforcementLearning (String filename, boolean SARSA) {
		r = new Random(System.currentTimeMillis());
		e = 0.5;
		isSARSA = SARSA;
		loadQ(filename);
	}
	
	/**
	 * Set current state
	 * @param cState current state
	 */
	public void setCurrentState(int cState) {
		currentState = cState;
	}
	
	/**
	 * Get action 
	 * @return encoded action
	 */
	public int takeAction() {
		if (!isSARSA) {
			if (r.nextDouble() < e) {
				currentAction = r.nextInt(numberOfActions);
			}
			else {
				currentAction = getArgMaxQ(currentState);
			}
		}
		else {
			currentAction = nextAction;
		}
		return currentAction;
	}
	
	/**
	 * Update Q table
	 * @param nState Current state
	 * @param reward Reward of transition from last state to current state 
	 */
	public void update(int nState, double reward) {
		nextState = nState;
		if (isSARSA) {
			if (r.nextDouble() < e) {
				nextAction = r.nextInt(numberOfActions);
			}
			else {
				nextAction = getArgMaxQ(nState);
			}
		}
		else {
			nextAction = getArgMaxQ(nState);
		}
		
		double QStateAction = (1-alpha)*getHashQValue(currentState, currentAction) + 
				alpha*(reward + gamma*getHashQValue(nState, nextAction));
		setHashQValue(currentState, currentAction, QStateAction);
		
		currentState = nextState;
	}
	
	/**
	 * Write weights into text file
	 * @param filename
	 */
	public void writeQToFile(String filename) {
		try {
			FileWriter writer = new FileWriter(filename, false);
			PrintWriter print = new PrintWriter(writer);
			
			print.println(numberOfActions);
			
			for (Integer key: hashQ.keySet()) {
				print.println(key);
				for (int j=0; j<hashQ.get(key).length; j++) {
					print.println(hashQ.get(key)[j]);
				}
			}
			print.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Load weights from text file
	 * @param filename text file that stores weights
	 */
	public void loadQ(String filename) {
		try {
			String line = null;
			FileReader reader = new FileReader(filename);
			BufferedReader bufferedReader = new BufferedReader(reader);
			line = bufferedReader.readLine();
			if (line != null) {
				numberOfActions = Integer.parseInt(line);
			}
			hashQ = new HashMap<Integer, double[]>();
			int index = 0;
			int key = 0;
			while ((line = bufferedReader.readLine()) != null) {
				if (index%numberOfActions == 0) {
					key = Integer.parseInt(line);
					hashQ.put(key, new double[numberOfActions]);
					line = bufferedReader.readLine();
					if (line == null) break;
				}
				hashQ.get(key)[index%numberOfActions] = Double.parseDouble(line);
				index++;
			}
			bufferedReader.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	protected int getArgMaxQ(int state) {
		double[] QState = getHashQValue(state);
		int action = 0;
		double r = QState[action];
		for (int i=0; i<QState.length; i++) {
			if (QState[i] > r) {
				r = QState[i];
				action = i;
			}
		}
		return action;
	}
	
	// Helper method to set hashQ
	private double getHashQValue(int state, int action) {
		if (!hashQ.containsKey(state)) {
			hashQ.put(state, new double[numberOfActions]);
			for (int i=0; i<hashQ.get(state).length; i++) {
				hashQ.get(state)[i] = 0;
			}
		}
		return hashQ.get(state)[action];
	}
	// Helper method to get hashQ
	private void setHashQValue(int state, int action, double value) {
		if (!hashQ.containsKey(state)) {
			hashQ.put(state, new double[numberOfActions]);
			for (int i=0; i<hashQ.get(state).length; i++) {
				hashQ.get(state)[i] = 0;
			}
		}
		hashQ.get(state)[action] = value;
	}
	
	private double[] getHashQValue(int state) {
		if (!hashQ.containsKey(state)) {
			hashQ.put(state, new double[numberOfActions]);
			for (int i=0; i<hashQ.get(state).length; i++) {
				hashQ.get(state)[i] = 0;
			}
		}
		return hashQ.get(state);
	}
}