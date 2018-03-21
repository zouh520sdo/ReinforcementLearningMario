package gameai.ReinforcementLearningMario;

import ch.idsia.agents.Agent;
import ch.idsia.agents.controllers.BasicMarioAIAgent;
import ch.idsia.benchmark.mario.environments.Environment;

public class MarioRLController extends BasicMarioAIAgent implements Agent {

	public ReinforcementLearning RL;
	protected double reward;
	protected int state;
	
	public MarioRLController(String s) {
		super("ReinforcementLearning");
		// TODO Auto-generated constructor stub
		int numberOfActions = (int)Math.pow(2, Environment.numberOfButtons);
		int stateSize = 1000;
		RL = new ReinforcementLearning(stateSize, numberOfActions, true);
		reward = 0.0;
	}
	
	public int getReceptiveEnemyCellValue(int x, int y)
	{
	    if (x < 0 || x >= enemies.length || y < 0 || y >= enemies[0].length)
	        return 0;

	    return enemies[x][y];
	}
	
	public int getMarioStateValue(int x)
	{
	    if (x < 0 || x >= marioState.length)
	        return 0;

	    return marioState[x];
	}
	
	@Override
	public boolean[] getAction() {
		int encodedStates = encodeStates();
		
		RL.update(encodedStates, reward);
		int encodedActions = RL.takeAction();
		action = decodeActions(encodedActions);
		return action;
	}
	
	public int encodeStates() {
		return 0;
	}

	public boolean[] decodeActions(int encodedActions) {
		boolean[] output = new boolean[Environment.numberOfButtons];
		int index = 0;
		while (encodedActions != 0 && index < output.length) {
			output[index] = encodedActions % 2 == 0 ? false : true;
			encodedActions /= 2;
			++index;
		}
		return output;
	}
}
