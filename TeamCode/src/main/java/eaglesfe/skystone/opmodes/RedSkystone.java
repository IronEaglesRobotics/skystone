package eaglesfe.skystone.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import java.util.HashMap;
import java.util.Map;

import eaglesfe.common.Step;
import eaglesfe.common.Steps;

@Autonomous (name = "red skystone", group = "red competition")
public class RedSkystone extends LinearOpMode {

    int tempTicks = 0;
    int moveMod = 0;
    int moveModTwo = 0;
    int position = 1;

    @Override
    public void runOpMode() {
        final skystoneRobot robot = new skystoneRobot(hardwareMap);

        /*================================================================================================*/

        //map of the steps
        Map<String, Step> steps = new HashMap<>();

        steps.put("start", new Step("moving forward..", 3500) {
            @Override
            public void enter() {
                robot.setArmPosition(.3, .3);
            }

            @Override
            public boolean isFinished() {
                return robot.drive.straightMove(22, .5,RedSkystone.this);
            }

            @Override
            public String leave() {
                robot.drive.setInput(0,0,0);
                return "arm down";
            }
        });

        steps.put("arm down", new Step("homing arm...") {
            @Override
            public void enter() {
                robot.setArmPosition(-.1, .1);
            }

            @Override
            public boolean isFinished() {
                return robot.armHomed(RedSkystone.this);
            }

            @Override
            public String leave() {
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
                        position = 0;
                        return "center";
                    case 1 :
                        robot.drive.setInput(0,0,0);
                        position = 4;
                        return "right";
                    case 2 :
                        robot.drive.setInput(0,0,0);
                        position = 8;
                        return "left";
                    default:
                        robot.drive.setInput(0,0,0);
                        return "center";
                }
            }

        });

        steps.put("left", new Step("moving to left...",1900) {
            @Override
            public void enter() {
                moveMod = - 20;
            }

            @Override
            public boolean isFinished() {
                return robot.corectingStrafe(2900, -.2, RedSkystone.this);
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
                moveMod = - 14;
            }

            @Override
            public boolean isFinished() {
                return robot.corectingStrafe(1600, -.2, RedSkystone.this);
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
                moveMod = -6;
            }

            @Override
            public boolean isFinished() {
                return robot.corectingStrafe(1650, .2, RedSkystone.this);
            }

            @Override
            public String leave() {
                robot.drive.setInput(0,0,0);
                return "suck";
            }
        });

        steps.put("suck", new Step("nice...", 1750) {
            @Override
            public void enter() {
                robot.drive.recordPoint();
                robot.setArmPosition(-.04,.1);
                robot.setIntakeSpeed(1);
            }

            @Override
            public boolean isFinished() {
                return robot.suckStraight(.35,RedSkystone.this);
            }

            @Override
            public String leave() {
                robot.drive.setInput(0,0,0);
                robot.setIntakeSpeed(0);
                return "reposition";
            }
        });

        steps.put("reposition", new Step("repostion block...", 1400) {
            @Override
            public void enter() {
            }

            @Override
            public boolean isFinished() {
                return robot.drive.straightMove(-robot.drive.inchDifference(), RedSkystone.this);
            }

            @Override
            public String leave() {
                robot.drive.setInput(0,0,0);
                return "turn left";
            }
        });

        steps.put("turn left", new Step("turning left..", 2300) {
            @Override
            public void enter() {
                robot.setArmPosition(-.04,.1);
            }

            @Override
            public boolean isFinished() {
                return robot.angleTurnAbsolute(90, RedSkystone.this);
            }

            @Override
            public String leave() {
                robot.drive.setInput(0,0,0);
                return "move forward";
            }
        });

        steps.put("move forward", new Step("move forward", 5000) {
            @Override
            public void enter() {
            }

            @Override
            public boolean isFinished() {
                return robot.drive.straightMove(-70 + moveMod, RedSkystone.this);
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
                return robot.angleTurnAbsolute(180,RedSkystone.this);
            }

            @Override
            public String leave() {
                robot.drive.setInput(0,0,0);
                return "align to foundation";
            }
        });

        steps.put("align to foundation", new Step("to foundation...", 1500) {
            @Override
            public void enter() {
            }

            @Override
            public boolean isFinished() {
                return robot.drive.straightMove(-11,RedSkystone.this);
            }

            @Override
            public String leave() {
                return "foundation wait";
            }
        });

        steps.put("foundation wait", new Step("waiting...",300) {
            @Override
            public void enter() {
                robot.foundationGrab(true);
            }

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public String leave() {
                return "grab foundation";
            }
        });

        steps.put("grab foundation", new Step("grabbing foundation...", 4000) {
            @Override
            public void enter() {
                robot.setIntakeSpeed(-1);
            }

            @Override
            public boolean isFinished() {
                return robot.drive.straightMove(16,RedSkystone.this);
            }

            @Override
            public String leave() {
                robot.setIntakeSpeed(0);
                return "turn foundation";
            }
        });

        steps.put("turn foundation", new Step("turning foundation...", 2000) {
            @Override
            public void enter() {
            }

            @Override
            public boolean isFinished() {
                return robot.angleTurnAbsolute(90,RedSkystone.this);
            }

            @Override
            public String leave() {
                robot.drive.setInput(0,0,0);
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
                return "park";
            }
        });

        steps.put("park", new Step("parking...") {
            @Override
            public void enter() {

            }

            @Override
            public boolean isFinished() {
                return robot.drive.straightMove(36,RedSkystone.this);
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
