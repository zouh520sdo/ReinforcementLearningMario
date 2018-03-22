package gameai_huang.RL.ReinforcementLearningMario;

import ch.idsia.agents.Agent;
import ch.idsia.agents.controllers.BasicMarioAIAgent;
import ch.idsia.benchmark.mario.engine.sprites.Mario;
import ch.idsia.benchmark.mario.environments.Environment;

public class QLAgent extends BasicMarioAIAgent implements Agent{

	public QLAgent(String s) {
		super(s);
		// TODO Auto-generated constructor stub
	}

	/**
	 * readme:
	 * getState is the only method u should call
	 * all other methods are just helpers of getState
	 */
	
	private float[] LastMarioPos = {0,0};
	private int stuckCount = 0;
	
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
	
	String b2s(boolean b) {
		if(b) return "1";
		else return "0";
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
	
	boolean searchAreaForEnemy(int xLow, int xHigh, int yLow, int yHigh) {
		for(int x = xLow; x < xHigh; x++)
			for(int y = yLow; y < yHigh; y++)
				if(enemies[y][x] != 0)
					return true;
		return false;
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

	@Override
	public boolean[] getAction() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void integrateObservation(Environment environment) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void giveIntermediateReward(float intermediateReward) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setName(String name) {
		// TODO Auto-generated method stub
		
	}
}
