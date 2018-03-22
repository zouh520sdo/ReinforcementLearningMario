package gameai.ReinforcementLearningMario;

import ch.idsia.agents.Agent;
import ch.idsia.agents.controllers.BasicMarioAIAgent;
import ch.idsia.benchmark.mario.engine.GlobalOptions;
import ch.idsia.benchmark.mario.environments.Environment;

public class MarioRLController extends BasicMarioAIAgent implements Agent {

	public ReinforcementLearning RL;
	protected double reward;
	protected int state;
	private float[] LastMarioPos = {0,0};
	private int stuckCount = 0;
	private int killCount = 0;
	private int LastMarioMode = marioMode;
    public boolean isFirstFrame;
	
	public MarioRLController(String s) {
		super("ReinforcementLearning");
		// TODO Auto-generated constructor stub
		int numberOfActions = (int)Math.pow(2, Environment.numberOfButtons);
		if (s == null) {
			RL = new ReinforcementLearning(numberOfActions, true);
		}
		else {
			RL = new ReinforcementLearning(s, true);
		}
		reward = 0.0;
		isFirstFrame = true;
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
		int reward = MoveReward();
		int encodedStates = getState();
		if (isFirstFrame) {
			reward = 0;
			RL.setCurrentState(encodedStates);
			isFirstFrame = false;
		}
		RL.update(encodedStates, reward);
		int encodedActions = RL.takeAction();
		action = decodeActions(encodedActions);
		return action;
	}
	
	public int encodeStates() {
		return 0;
	}
	
	/////////////////////////////////////////////////////////////////////////
	////////////////// Encode state to integer --- Hongyang /////////////////
	int getState() {
		String stateBinary = "";
		// 0~1 Fire/Big/Small
		stateBinary = stateBinary.concat(s_mode());
		// 2~4 isMarioOnGround, isMarioAbleToJump, isMarioAbleToShoot
		stateBinary = stateBinary.concat(b2s(isMarioOnGround));
		stateBinary = stateBinary.concat(b2s(isMarioAbleToJump));
		stateBinary = stateBinary.concat(b2s(isMarioAbleToShoot));
		// 5 stuck
		stateBinary = stateBinary.concat(s_stuck());
		// 6~9 direction
		stateBinary = stateBinary.concat(s_dir());
		// 10~13 enemy immediately up/down/left/right
		stateBinary = stateBinary.concat(s_enemy_close());
		// 14~17 enemy within 3 units up/down/left/right
		stateBinary = stateBinary.concat(s_enemy_mid());
		// 18~23 obstacles
		stateBinary = stateBinary.concat(s_obstacles());
		
		if(LastMarioPos[0] == marioFloatPos[0] && 
			LastMarioPos[1] == marioFloatPos[1]) {
			stuckCount++;
		}
		
		LastMarioPos[0] = marioFloatPos[0];
		LastMarioPos[1] = marioFloatPos[1];
		
		return Integer.parseInt(stateBinary, 2);
	}
	
	String s_mode() {
		String s = Integer.toBinaryString(marioMode); 
		if(s.length() < 2)
			s = "0".concat(s);
		return s;
	}
	
	String s_stuck() {
		if(stuckCount > 10)
			return "1";
		else 
			return "0";
	}
	
	String s_dir() {
		float deltaX = marioFloatPos[0] - LastMarioPos[0];
		float deltaY = marioFloatPos[1] - LastMarioPos[1];
		
		if(deltaX == 0 && deltaY == 0) return "0000";
		else if (deltaX == 0 && deltaY > 0) return "0001";
		else if (deltaX == 0 && deltaY < 0) return "0010";
		else if (deltaX > 0 && deltaY == 0) return "0011";
		else if (deltaX > 0 && deltaY > 0) return  "0100";
		else if (deltaX > 0 && deltaY < 0) return "0101";
		else if (deltaX < 0 && deltaY == 0) return "0110";
		else if (deltaX < 0 && deltaY > 0) return "0111";
		else return "1111";
	}
	
	String s_enemy_close() {
		String s = "";
		s = s.concat(b2s(enemies[marioCenter[0]][marioCenter[1] + 1] != 0));
		s = s.concat(b2s(enemies[marioCenter[0]][marioCenter[1] - 1] != 0));
		s = s.concat(b2s(enemies[marioCenter[0] + 1][marioCenter[1]] != 0));
		if(marioMode == 0)
			s = s.concat(b2s(enemies[marioCenter[0] - 1][marioCenter[1]] != 0));
		else
			s = s.concat(b2s(enemies[marioCenter[0] - 2][marioCenter[1]] != 0));
		return s;
	}
	
	String s_enemy_mid() {
		String s = "";
		s = s.concat(b2s(searchAreaForEnemy(marioCenter[1]+1, marioCenter[1]+4, marioCenter[0]-2, marioCenter[0]+2)));
		s = s.concat(b2s(searchAreaForEnemy(marioCenter[1]-4, marioCenter[1]-1, marioCenter[0]-2, marioCenter[0]+2)));
		s = s.concat(b2s(searchAreaForEnemy(marioCenter[1]-2, marioCenter[1]+2, marioCenter[0]+1, marioCenter[0]+4)));
		
		if(marioMode == 0)
			s = s.concat(b2s(searchAreaForEnemy(marioCenter[1]-2, marioCenter[1]+2, marioCenter[0]-4, marioCenter[0]-1)));
		else
			s = s.concat(b2s(searchAreaForEnemy(marioCenter[1]-2, marioCenter[1]+2, marioCenter[0]-5, marioCenter[0]-2)));
		return s;
	}
	
	// check wall in front of mario, to save space, only check left once
	String s_obstacles() {
		String s = "";
		s = s.concat(b2s(levelScene[marioCenter[0]+1][marioCenter[1]+1] != 0));
		s = s.concat(b2s(levelScene[marioCenter[0]][marioCenter[1]+1] != 0));
		s = s.concat(b2s(levelScene[marioCenter[0]-1][marioCenter[1]+1] != 0));
		s = s.concat(b2s(levelScene[marioCenter[0]-2][marioCenter[1]+1] != 0));
		s = s.concat(b2s(levelScene[marioCenter[0]-3][marioCenter[1]+1] != 0));
		s = s.concat(b2s(levelScene[marioCenter[0]][marioCenter[1]-1] != 0));
		
		return s;
	}
	
	String b2s(boolean b) {
		if(b) return "1";
		else return "0";
	}
	
	boolean searchAreaForEnemy(int xLow, int xHigh, int yLow, int yHigh) {
		for(int x = xLow; x < xHigh; x++)
			for(int y = yLow; y < yHigh; y++)
				if(enemies[y][x] != 0)
					return true;
		return false;
	}
	/////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////
	
	int getReward() {
		int reward = -1;
		reward += MoveReward();
		reward += KillReward();
		reward -= DamagePunish();
		reward -= StuckPunish();
		
		killCount = getKillsTotal;
		LastMarioMode = marioMode;
		return reward;
	}
	
	int MoveReward() {
		int reward = -1;
		float deltaX = marioFloatPos[0] - LastMarioPos[0];
		float deltaY = marioFloatPos[1] - LastMarioPos[1];
		if(deltaX > 0) reward += 100;
		if(deltaY > 0) reward += 40;
		return reward;
	}
	
	int KillReward() {
		if(killCount < getKillsTotal) return 200*(getKillsTotal - killCount);
		else return 0;
	}
	
	int DamagePunish() {
		return (LastMarioMode-marioMode)*500;
	}
	
	int StuckPunish() {
		if(s_stuck() == "1") return 100;
		else return 0;
	}
	
	/////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////
	
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
