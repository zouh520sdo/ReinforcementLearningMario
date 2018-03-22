package gameai_huang.RL.ReinforcementLearningMario;

import ch.idsia.benchmark.mario.engine.GlobalOptions;
import ch.idsia.benchmark.mario.environments.Environment;
import ch.idsia.benchmark.mario.environments.MarioEnvironment;
import ch.idsia.tools.CmdLineOptions;
import gameai.ESnew;

public class RLTest {
	public static void main(String[] args)
    {
		String outputFile = "QTest.txt";
    	int level = 0;
    	Environment environment = MarioEnvironment.getInstance();
    	MarioRLController el = new MarioRLController(null);
		String argsString = "-vis off -ld " + level + " -ag MarioRLController";
		CmdLineOptions cmdLineOptions = new CmdLineOptions(argsString);
        cmdLineOptions.setPauseWorld(false);
        cmdLineOptions.setVisualization(true);
        cmdLineOptions.setAgent(el);
        System.out.println("Reinforcement learning starts ");
	    environment.reset(cmdLineOptions);
	    while (!environment.isLevelFinished())
	    {
	        environment.tick();
	        if (!GlobalOptions.isGameplayStopped)
	        {
	        	cmdLineOptions.getAgent().integrateObservation(environment);
	        	cmdLineOptions.getAgent().giveIntermediateReward(environment.getIntermediateReward());

	            boolean[] action = cmdLineOptions.getAgent().getAction();
	            environment.performAction(action);
	    		el.RL.writeQToFile(outputFile);
	        }
	    }
	    environment.closeRecorder();
	    environment.getEvaluationInfo().setTaskName("MyTask");
    }
}
