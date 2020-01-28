package eaglesfe.skystone.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import java.util.HashMap;
import java.util.Map;

import eaglesfe.common.Step;
import eaglesfe.common.Steps;

@Autonomous (name = "test", group = "blue competition")
public class testAuto extends LinearOpMode{

    @Override
    public void runOpMode() {
        final skystoneRobot robot = new skystoneRobot(hardwareMap);

        //map of the steps
        Map<String, Step> steps = new HashMap<>();

        //steps
        steps.put("start", new Step("starting...") {
            @Override
            public void enter() {
            }

            @Override
            public boolean isFinished() {
                return true;
            }

            @Override
            public String leave() {
                return "turn1";
            }
        });

        steps.put("turn1", new Step("turning...") {
            @Override
            public void enter() {

            }

            @Override
            public boolean isFinished() {
                return robot.angleTurnRelative(90,5, testAuto.this);
            }

            @Override
            public String leave() {
                sleep(500);
                return "turn2";
            }
        });

        steps.put("turn2", new Step("turning...") {
            @Override
            public void enter() {

            }

            @Override
            public boolean isFinished() {
                return robot.angleTurnRelative(-90, .5, testAuto.this);
            }

            @Override
            public String leave() {
                sleep(500);
                return "turn1";
            }
        });

        steps.put("end", new Step("ending...") {
            @Override
            public void enter() {

            }

            @Override
            public boolean isFinished() {
                return true;
            }

            @Override
            public String leave() {
                return null;
            }
        });

        //wait for the auto to be started
        waitForStart();

        //steps runner
        Steps stepsRunner = new Steps(steps, this);
        stepsRunner.runStepsMap();

        //end of autonomous
        robot.stopAllMotors();
    }
}
