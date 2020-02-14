package eaglesfe.skystone.opmodes.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import java.util.HashMap;
import java.util.Map;

import eaglesfe.common.Step;
import eaglesfe.common.Steps;
import eaglesfe.skystone.skystoneRobot;

@Autonomous (name = "park", group = "park")
public class Park extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {

        final skystoneRobot robot = new skystoneRobot(hardwareMap);

        //map of the steps
        Map<String, Step> steps = new HashMap<>();

        waitForStart();

        steps.put("start", new Step("lifting arm...") {
            @Override
            public void enter() {
                robot.setArmPosition(.2, .3);
            }

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public String leave() {
                return "home";
            }
        });

        steps.put("home", new Step("homing arm...") {
            @Override
            public void enter() {
                robot.setArmPosition(-.1, .1);
            }

            @Override
            public boolean isFinished() {
                return robot.armHomed(Park.this);
            }

            @Override
            public String leave() {
                return "move under bridge";
            }
        });

        steps.put("move under bridge", new Step("parking...") {
            @Override
            public void enter() {
            }

            @Override
            public boolean isFinished() {
                return robot.drive.straightMove(-20, Park.this);
            }

            @Override
            public String leave() {
                return "end";
            }
        });

        steps.put("end", new Step("end") {
            @Override
            public void enter() {
                robot.stopAllMotors();
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

        //telemetry
        telemetry.addData("intitialized", "");
        telemetry.update();

        //once started start running through the steps
        Steps stepsRunner = new Steps(steps, this);


        stepsRunner.runStepsMap();

        /*================================================================================================*/

        //end of autonomous
        robot.stopAllMotors();
    }
}
