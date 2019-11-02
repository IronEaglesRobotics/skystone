package eaglesfe.skystone.opmodes;

import com.eaglesfe.birdseye.BirdseyeServer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.internal.opmode.RegisteredOpModes;

import java.util.HashMap;
import java.util.Map;

import eaglesfe.roverruckus.opmodes.OpModeHelpers;
import eaglesfe.common.SleepStep;
import eaglesfe.common.Step;
import eaglesfe.common.Steps;

@Autonomous (name = "competition auto", group = "competition")
public class skystoneAuto extends LinearOpMode {

    @Override
    public void runOpMode() {
        final BirdseyeServer server = new BirdseyeServer(3708, telemetry);
        final skystoneRobot robot = new skystoneRobot(hardwareMap);
        robot.setVisionEnabled(true);

/*================================================================================================*/

        //map of the steps
        Map<String, Step> steps = new HashMap<>();

        //test of auto by moving in a square

        steps.put("forward", new Step("moves forward 10 in") {
            @Override
            public void enter() { robot.drive.setTargetPositionRelative(8,.5);}

            @Override
            public boolean isFinished() {
                 return !robot.drive.isBusy();
            }

            @Override
            public String leave() {
                return "";
            }
        });

        steps.put("strafe right", new Step("strafes to first skystone in") {
            @Override
            public void enter() {
                robot.setDriveInputX(.25);
            }

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public String leave() {
                //maybe need to open the claw here
                return "extend arm";
            }
        });

        steps.put("extend arm", new Step("moves arm down to pick up the 1st skystone") {
            @Override
            public void enter() {
            }

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public String leave() {
                return "claw grab";
            }
        });

        steps.put("claw grab", new Step("grabs the skystone") {
            @Override
            public void enter() {
            }

            @Override
            public boolean isFinished() {
                return !robot.drive.isBusy();
            }

            @Override
            public String leave() {
                return  "move with block";
            }
        });

        steps.put("move with block", new Step("tips arm back, turns, and drops block") {
            @Override
            public void enter() {

            }

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public String leave() {
                //maybe drop the block here
                return "retract arm";
            }
        });

        steps.put("retract arm", new Step("put the arm under 14in") {
            @Override
            public void enter() {

            }

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public String leave() {
                return "push block";
            }
        });

        steps.put("push block", new Step("push the block all the way under") {
            @Override
            public void enter() {

            }

            @Override
            public boolean isFinished() {
                return false;
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
                return !robot.drive.isBusy();
            }

            @Override
            public String leave() {
                return null;
            }
        });

        //wait for the auto to be started
        waitForStart();

        //once started start running through the steps
        Steps stepsRunner = new Steps(steps, this);
        stepsRunner.runStepsMap();

/*================================================================================================*/

        //end of autonomous
        robot.stopAllMotors();

    }
}
