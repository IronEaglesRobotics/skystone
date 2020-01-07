package eaglesfe.skystone.opmodes;

import android.widget.Switch;

import com.eaglesfe.birdseye.BirdseyeServer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcontroller.external.samples.ConceptDIMAsIndicator;
import org.firstinspires.ftc.robotcore.internal.opmode.RegisteredOpModes;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import eaglesfe.common.Step;
import eaglesfe.common.Steps;

@Autonomous (name = "blue skystone", group = "blue competition")
public class BlueSkystone extends LinearOpMode {

    int moveMod = 0;
    int moveModTwo = 0;

    @Override
    public void runOpMode() {
        final skystoneRobot robot = new skystoneRobot(hardwareMap);
        robot.setVisionEnabled(true);
        robot.useCameraVuforia();

/*================================================================================================*/

        //map of the steps
        Map<String, Step> steps = new HashMap<>();

        steps.put("start", new Step("moving forward..", 2000) {
            @Override
            public void enter() {
                robot.settleAngle();
                robot.drive.setTargetPositionRelative(23,.5);
                robot.setArmPosition(.3, .3);
            }

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public String leave() {
                robot.drive.setInput(0,0,0);
                return "back a little";
            }
        });

        steps.put("back a little", new Step("BAAACK...",400) {
            @Override
            public void enter() {

            }

            @Override
            public boolean isFinished() {
                sleep(500);
                return true;
            }

            @Override
            public String leave() {
                int locate = (int) robot.locateSkystone();
                switch (locate) {
                    case 0 :
                        robot.drive.setInput(0,0,0);
                        return "left";
                    case 1 :
                        robot.drive.setInput(0,0,0);
                        return "center";
                    case 2 :
                        robot.drive.setInput(0,0,0);
                        return "right";
                    default:
                        robot.drive.setInput(0,0,0);
                        return "center";
                }
            }

        });

        steps.put("left", new Step("moving to left...",1900) {
            @Override
            public void enter() {
            }

            @Override
            public boolean isFinished() {
                return robot.corectingStrafe(1200, -.2, BlueSkystone.this);
            }

            @Override
            public String leave() {
                robot.drive.setInput(0,0,0);
                return "suck";
            }
        });

        steps.put("center", new Step("moving to center...",2500) {
            @Override
            public void enter() {
                moveMod = - 8;
            }

            @Override
            public boolean isFinished() {
               return robot.corectingStrafe(1200, .2, BlueSkystone.this);
            }

            @Override
            public String leave() {
                robot.drive.setInput(0,0,0);
                return "suck";
            }
        });

        steps.put("right", new Step("moving to right...",3000) {
            @Override
            public void enter() {
                moveMod = - 18;
            }

            @Override
            public boolean isFinished() {
                return robot.corectingStrafe(2200, .2, BlueSkystone.this);
            }

            @Override
            public String leave() {
                robot.drive.setInput(0,0,0);
                return "suck";
            }
        });

        steps.put("suck", new Step("nice...", 5000) {
            @Override
            public void enter() {
                robot.drive.setTargetPositionRelative(15,.1);
                robot.setIntakeSpeed(-1);
            }

            @Override
            public boolean isFinished() {
                return !robot.isDriveBusy();
            }

            @Override
            public String leave() {
                robot.setIntakeSpeed(0);
                return "reposition";
            }
        });

        steps.put("reposition", new Step("repostion block...", 550) {
            @Override
            public void enter() {
                robot.setClawPosition(skystoneRobot.Constants.CLAWMID);
                robot.drive.setInput(0,-1,0);
            }

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public String leave() {
                robot.drive.setInput(0,0,0);
                robot.setArmPosition(.02, .3);
                return "turn left";
            }
        });

//        steps.put("deploy arm", new Step("deploying arm...") {
//            @Override
//            public void enter() {
//                robot.setClawPosition(skystoneRobot.Constants.CLAWMID);
//                robot.setArmPosition(.93, .3);
//            }
//
//            @Override
//            public boolean isFinished() {
//                return !robot.isArmBusy();
//            }
//
//            @Override
//            public String leave() {
//                return "grab one";
//            }
//        });
//
//        steps.put("grab one", new Step("grab block...", 300) {
//            @Override
//            public void enter() {
//                robot.setClawPosition(skystoneRobot.Constants.CLAWCLOSED);
//            }
//
//            @Override
//            public boolean isFinished() {
//                return false;
//            }
//
//            @Override
//            public String leave() {
//                return "move out of line";
//            }
//        });
//
//        steps.put("move out of line", new Step("moving out of line...", 600) {
//            @Override
//            public void enter() {
//                robot.drive.setInput(0,-.4,0);
//            }
//
//            @Override
//            public boolean isFinished() {
//                return false;
//            }
//
//            @Override
//            public String leave() {
//                robot.drive.setInput(0,0,0);
//                return "turn one";
//            }
//        });
//
//        steps.put("turn one", new Step("turn...", 800) {
//            @Override
//            public void enter() {
//                robot.setWristPosition(0);
//            }
//
//            @Override
//            public boolean isFinished() {
//                return false;
//            }
//
//            @Override
//            public String leave() {
//                return "open";
//            }
//        });
//
//        steps.put("open", new Step("opening...", 300) {
//            @Override
//            public void enter() {
//                robot.setClawPosition(skystoneRobot.Constants.CLAWOPEN);
//            }
//
//            @Override
//            public boolean isFinished() {
//                return false;
//            }
//
//            @Override
//            public String leave() {
//                return "turn two";
//            }
//        });
//
//        steps.put("turn two", new Step("turning back...",800) {
//            @Override
//            public void enter() {
//                robot.setWristPosition(skystoneRobot.Constants.WRISTMIDDLE);
//            }
//
//            @Override
//            public boolean isFinished() {
//                return false;
//            }
//
//            @Override
//            public String leave() {
//                return "grab two";
//            }
//        });
//
//        steps.put("grab two", new Step("close up",300) {
//            @Override
//            public void enter() {
//                robot.setClawPosition(skystoneRobot.Constants.CLAWCLOSED);
//            }
//
//            @Override
//            public boolean isFinished() {
//                return false;
//            }
//
//            @Override
//            public String leave() {
//                return "into robot";
//            }
//        });
//
//        steps.put("into robot", new Step("moving arm into bot...") {
//            @Override
//            public void enter() {
//                robot.setArmPosition(-.07, .3);
//            }
//
//            @Override
//            public boolean isFinished() {
//                return !robot.isArmBusy();
//            }
//
//            @Override
//            public String leave() {
//                robot.setClawPosition(skystoneRobot.Constants.CLAWMID);
//                return "turn left";
//            }
//        });

//        steps.put("turn left", new Step("turning left..", 1300) {
//            @Override
//            public void enter() {
//                robot.drive.setInput(0,0,.35);
//            }
//
//            @Override
//            public boolean isFinished() {
//                return false;
//            }
//
//            @Override
//            public String leave() {
//                robot.drive.setInput(0,0,0);
//                robot.setClawPosition(skystoneRobot.Constants.CLAWCLOSED);
//                robot.drive.setInput(0,0,0);
//                return "move forward";
//            }
//        });

        steps.put("turn left", new Step("turning left..", 2300) {
            @Override
            public void enter() {
            }

            @Override
            public boolean isFinished() {
                return robot.angleTurnRelative(90, .2,BlueSkystone.this);
            }

            @Override
            public String leave() {
                robot.drive.setInput(0,0,0);
                robot.setClawPosition(skystoneRobot.Constants.CLAWCLOSED);
                robot.drive.setInput(0,0,0);
                return "move forward";
            }
        });

        steps.put("move forward", new Step("move forward", 6000) {
            @Override
            public void enter() {
                robot.drive.setTargetPositionRelative(-65 + moveMod,.75);
            }

            @Override
            public boolean isFinished() {
                return !robot.drive.isBusy();
            }

            @Override
            public String leave() {
                robot.drive.setInput(0,0,0);
                return "turn to foundation";
            }
        });

        steps.put("turn to foundation", new Step("turning to foundation...", 1300) {
            @Override
            public void enter() {
            }

            @Override
            public boolean isFinished() {
                return robot.angleTurnRelative(90, .2, BlueSkystone.this);
            }

            @Override
            public String leave() {
                robot.drive.setInput(0,0,0);
                return "align to foundation";
            }
        });

        steps.put("align to foundation", new Step("to foundation...") {
            @Override
            public void enter() {
                robot.drive.setTargetPositionRelative(-10,.25);
            }

            @Override
            public boolean isFinished() {
                return !robot.drive.isBusy();
            }

            @Override
            public String leave() {
                return "grab foundation";
            }
        });

        steps.put("grab foundation", new Step("grabbing foundation...", 1500) {
            @Override
            public void enter() {
                robot.drive.setTargetPositionRelative(-3,.15);
                robot.foundationGrab(true);
            }

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public String leave() {
                return "reposition foundation";
            }
        });

        steps.put("reposition foundation", new Step("moving the foundation", 750) {
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
                return "turn foundation";
            }
        });

        steps.put("turn foundation", new Step("turning foundation...", 2000) {
            @Override
            public void enter() {
                robot.drive.setInput(0,0,-.4);
            }

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public String leave() {
                return "to wall";
            }
        });

        steps.put("to wall", new Step("pushing to wall...", 750) {
            @Override
            public void enter() {
                robot.drive.setInput(0,-.4,0);
            }

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public String leave() {
                robot.foundationGrab(true);
                return "move close to bridge";
            }
        });

        steps.put("move close to bridge", new Step("moving to bridge", 750) {
            @Override
            public void enter() {
                robot.drive.setInput(0,.4,0);
            }

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public String leave() {
                robot.drive.setInput(0,0,0);
                return "block out";
            }
        });

        steps.put("block out", new Step("putting block out") {
            @Override
            public void enter() {
                robot.setArmPosition(.95, .3);
            }

            @Override
            public boolean isFinished() {
                return !robot.isArmBusy();
            }

            @Override
            public String leave() {
                robot.setClawPosition(skystoneRobot.Constants.CLAWOPEN);
                return "save claw";
            }
        });

        steps.put("save claw", new Step("stop breaking robot...") {
            @Override
            public void enter() {
                robot.setArmPosition(.7, .3);
            }

            @Override
            public boolean isFinished() {
                return !robot.isArmBusy();
            }

            @Override
            public String leave() {
                robot.setClawPosition(skystoneRobot.Constants.CLAWCLOSED);
                return "arm in";
            }
        });

        steps.put("arm in", new Step("putting away our arm...") {
            @Override
            public void enter() {
                robot.setArmPosition(-.05, .3);
            }

            @Override
            public boolean isFinished() {
                return !robot.isArmBusy();
            }

            @Override
            public String leave() {
                return "park";
            }
        });

        steps.put("park", new Step("parking...", 500) {
            @Override
            public void enter()
            {
                robot.drive.setInput(0,.5,0);
            }

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public String leave() {
                robot.drive.setInput(0,0,0);
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
