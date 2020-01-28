package eaglesfe.skystone.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import java.util.HashMap;
import java.util.Map;

import eaglesfe.common.Step;
import eaglesfe.common.Steps;

@Autonomous (name = "Saber blue foundation", group = "blue competition")
public class SaberBlueFoundation extends LinearOpMode {

    private long startingTime;

    @Override
    public void runOpMode() {
        final skystoneRobot robot = new skystoneRobot(hardwareMap);

        //map of the steps
        Map<String, Step> steps = new HashMap<>();

        steps.put("start", new Step("strafe to the left...") {
            @Override
            public void enter() {
                startingTime = System.currentTimeMillis();
                robot.setArmPosition(-.05,.2);
            }

            @Override
            public boolean isFinished() {
                return robot.corectingStrafe(1400, .4, SaberBlueFoundation.this);
            }

            @Override
            public String leave() {
                return "initial forward";
            }
        });

        steps.put("initial forward", new Step("moving forward...") {
            @Override
            public void enter() {

            }

            @Override
            public boolean isFinished() {
                return robot.drive.straightMove(-25, SaberBlueFoundation.this);
            }

            @Override
            public String leave() {
                return "grab foundation";
            }
        });

        steps.put("grab foundation", new Step("repositioning foundation...", 1200) {
            @Override
            public void enter() {
                robot.foundationGrab(true);
                robot.drive.setInput(0,-.2,0);
            }

            @Override
            public boolean isFinished() {
                return  false;
            }

            @Override
            public String leave() {
                return "corner";
            }
        });

        steps.put("corner", new Step("corner...", 2500) {
            @Override
            public void enter() {
            }

            @Override
            public boolean isFinished() {
                return robot.drive.straightMove(34, SaberBlueFoundation.this);
            }

            @Override
            public String leave() {
                robot.foundationGrab(true);
                return "wait";
            }
        });

        steps.put("wait", new Step("waiting...") {
            @Override
            public void enter() {

            }

            @Override
            public boolean isFinished() {
                return (System.currentTimeMillis() - startingTime) > 20000;
            }

            @Override
            public String leave() {
                return "small strafe";
            }
        });


        steps.put("small strafe", new Step("strafing...") {
            @Override
            public void enter() {

            }

            @Override
            public boolean isFinished() {
                return robot.corectingStrafe(3000, -.4, SaberBlueFoundation.this);
            }

            @Override
            public String leave() {
                return "small turn";
            }
        });

        steps.put("small turn", new Step("quick turn ", 1200) {
            @Override
            public void enter() {
                robot.setArmPosition(.3,.2);
                robot.drive.setInput(0,0,-.3);
            }

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public String leave() {
                robot.drive.setInput(0,0,0);
                return "arm down";
            }
        });

        steps.put("arm down", new Step("homing arm...", 4000) {
            @Override
            public void enter() {
                robot.setArmPosition(-.1, .1);
            }

            @Override
            public boolean isFinished() {
                return robot.armHomed(SaberBlueFoundation.this);
            }

            @Override
            public String leave() {
                return "park";
            }
        });

        steps.put("park", new Step("parking...", 1400) {
            @Override
            public void enter() {
                robot.drive.setInput(0,.5,0);
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
