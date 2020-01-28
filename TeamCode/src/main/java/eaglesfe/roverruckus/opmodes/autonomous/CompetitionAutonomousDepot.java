//package eaglesfe.roverruckus.opmodes.autonomous;
//
//import com.eaglesfe.birdseye.BirdseyeServer;
//
//import android.graphics.Point;
//
//import com.eaglesfe.birdseye.BirdseyeServer;
//import com.eaglesfe.birdseye.FieldPosition;
//import com.eaglesfe.birdseye.roverruckus.MineralSample;
//import com.eaglesfe.birdseye.util.MathHelpers;
//import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
//import com.qualcomm.robotcore.eventloop.opmode.Disabled;
//import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.Map;
//
//import eaglesfe.common.SleepStep;
//import eaglesfe.common.Step;
//import eaglesfe.common.Steps;
//import eaglesfe.roverruckus.Robot;
//import eaglesfe.roverruckus.opmodes.OpModeHelpers;
//
//@Autonomous(name="Autonomous Depot", group ="Competition")
//@Disabled
//public class CompetitionAutonomousDepot extends LinearOpMode {
//
//    private Enum LastGoldPositionFromLander;
//    private Enum GoldPositionFromLander;
//
//    @Override
//    public void runOpMode() {
//        final BirdseyeServer    server  =   new BirdseyeServer(3708, telemetry);
//        final Robot             robot   =   new Robot(hardwareMap);
//        robot.setVisionEnabled(true);
//
//        Map<String, Step> steps = new HashMap<>();
//
//        steps.put("start", new Step("Descending from lander...") {
//            public void enter() { robot.setLiftPosition(0.95, 1.0); }
//            public boolean isFinished() { return !robot.isLiftBusy(); }
//            public String leave() { return "scoot_away_from_hook"; }
//        });
//
//        steps.put("scoot_away_from_hook", new Step("Scoot away from hook...", 1000) {
//            public void enter() { robot.moveForward(5.0, 0.3); }
//            public boolean isFinished() { return !robot.isDriveBusy(); }
//            public String leave() {
//                robot.setDriveInput(0, 0, 0);
//                return "scoot_toward_minerals";
//            }
//        });
//
//        steps.put("scoot_toward_minerals", new Step("Quick scoot toward minerals...", 1300) {
//            public void enter() { robot.setDriveInput(-0.3, 0, 0);}
//            public boolean isFinished() { return false; }
//            public String leave() {
//                robot.stopAllMotors();
//                return "Move_to_last_gold_position";
//            }
//        });
//
//        steps.put("Move_to_last_gold_position", new Step("Move to last gold position", 300) {
//            @Override
//            public void enter() {
//                robot.stopAllMotors();
//            }
//
//            @Override
//            public boolean isFinished() {
//                return false;
//            }
//
//            @Override
//            public String leave() {
//                if (LastGoldPositionFromLander == MineralSample.GoldMineralArrangementFromLander.LEFT) {
//                    return "Move_to_left";
//                } else if (LastGoldPositionFromLander == MineralSample.GoldMineralArrangementFromLander.CENTER) {
//                    return "Move_to_center";
//                } else if (LastGoldPositionFromLander == MineralSample.GoldMineralArrangementFromLander.RIGHT) {
//                    return "Move_to_right";
//                } else {
//                    return "scan_for_vuforia_targets";
//                }
//            }
//        });
//
//        steps.put("Move_to_left", new Step("move to left") {
//            @Override
//            public void enter() {
//                robot.moveBackward(20,.5);
//            }
//
//            @Override
//            public boolean isFinished() {
//                return !robot.isDriveBusy();
//            }
//
//            @Override
//            public String leave() {
//                robot.stopAllMotors();
//                return "dislodge_gold_mineral";
//            }
//        });
//
//        steps.put("Move_to_center", new Step("move to center") {
//            @Override
//            public void enter() {
//                robot.moveBackward(3,.5);
//            }
//
//            @Override
//            public boolean isFinished() {
//                return !robot.isDriveBusy();
//            }
//
//            @Override
//            public String leave() {
//                robot.stopAllMotors();
//                return "dislodge_gold_mineral";
//            }
//        });
//
//        steps.put("Move_to_right", new Step("move to right") {
//            @Override
//            public void enter() {
//                robot.moveForward(12,.5);
//            }
//
//            @Override
//            public boolean isFinished() {
//                return !robot.isDriveBusy();
//            }
//
//            @Override
//            public String leave() {
//                robot.stopAllMotors();
//                return "dislodge_gold_mineral";
//            }
//        });
//
//        steps.put("dislodge_gold_mineral", new Step("Dislodge gold mineral...", 1300) {
//            public void enter() { robot.setDriveInput(-0.35, 0, 0); }
//            public boolean isFinished() { return false; }
//            public String leave() {
//                robot.setDriveInput(0, 0, 0);
//                return "sleep_to_settle_after_sample";
//            }
//        });
//
//        steps.put("sleep_to_settle_after_sample", new SleepStep("Pause to let motion settle...",
//                250, "return_to_previous_position"));
//
//        steps.put("return_to_previous_position", new Step("Return to previous position...", 1100) {
//            public void enter () { robot.setDriveInput(0.35,0,0); }
//            public boolean isFinished() { return false; }
//            public String leave () {
//                robot.setDriveInput(0,0,0);
//                return "scan_for_vuforia_targets";
//            }
//        });
//
//        steps.put("scan_for_vuforia_targets", new Step("Scan for vuforia targets...", 6000) {
//
//            public void enter () {
//                robot.useRearCamera();
//                robot.setDriveInput(0,-.1,0);
//            }
//
//            public boolean isFinished() {
//                FieldPosition position = robot.getPosition();
//                if (position != null) {
//                    return true;
//                } else {
//                    float heading = robot.getGyroHeading180();
//                    if (heading >= 1) {
//                        robot.setDriveInputZ(0.1);
//                    } else if (heading <= -1) {
//                        robot.setDriveInputZ(-0.1);
//                    } else {
//                        robot.setDriveInputZ(0);
//                    }
//                }
//                telemetry.update();
//                return false;
//            }
//
//            public String leave () {
//                robot.stopAllMotors();
//                return "move_to_wall";
//            }
//        });
//
//        steps.put("move_to_wall", new Step("Move to wall...", 10000) {
//
//            private double distance = Double.MIN_VALUE;
//            public void enter() { tryMove(); }
//
//            private void tryMove() {
//                FieldPosition position = robot.getPosition();
//
//                if (position != null) {
//                    int x = (int)position.getX();
//                    int y = (int)position.getY();
//                    Point target = OpModeHelpers.getTurnaroundPointForAutonomous(position);
//                    this.distance = MathHelpers.getDistanceBetweenTwoPoints(new Point(target.x, target.y), new Point(x, y));
//                    robot.moveBackward(this.distance, 0.3);
//                }
//            }
//
//            public boolean isFinished() {
//                if (distance == Double.MIN_VALUE) {
//                    tryMove();
//                } else {
//                    return !robot.isDriveBusy();
//                }
//                return false;
//            }
//
//            public String leave() {
//                robot.stopAllMotors();
//                return "turn_to_face_depot";
//            }
//        });
//
//        steps.put("turn_to_face_depot", new Step("Turn to face the depot...", 10000) {
//
//            public void enter() { robot.resetGyroHeading(); }
//
//            public boolean isFinished() {
//                if (robot.getGyroHeading180() < -110) {
//                    return true;
//                }
//
//                robot.setDriveInput(0,0, 0.4);
//                return false;
//            }
//
//            public String leave() {
//                robot.stopAllMotors();
//                return "strafe_toward_wall";
//            }
//        });
//
//        steps.put("strafe_toward_wall", new Step("Strafe toward wall to square up...", 1000) {
//            public void enter() { robot.setDriveInput(0.4, 0, 0);}
//            public boolean isFinished() { return false; }
//            public String leave() {
//                robot.stopAllMotors();
//                return "skedaddle";
//            }
//        });
//
//        steps.put("skedaddle", new Step("Skedaddle post haste to the depot...") {
//            public void enter() {
//                robot.setArmPosition(Robot.Constants.TEAM_MARKER_DEPLOY, 1.0);
//                robot.moveBackward(50, 0.6);
//            }
//            public boolean isFinished() { return !robot.isDriveBusy() && !robot.isArmBusy(); }
//            public String leave() {
//                robot.stopAllMotors();
//                return "square_crater";
//            }
//        });
//
//        steps.put("square_crater", new Step("square to crater", 400) {
//            @Override
//            public void enter() {
//                robot.setDriveInput(0.4, 0, 0);
//            }
//
//            @Override
//            public boolean isFinished() {
//                return false;
//            }
//
//            @Override
//            public String leave() {
//                robot.stopAllMotors();
//                return "crater";
//            }
//        });
//
//        steps.put("crater", new Step("CRATER!!!") {
//            public void enter() {
//                robot.setArmPosition(0.20, 1.0);
//                robot.moveForward(58, 0.6);
//                robot.setExtedPosition(-4500, 1.0);
//            }
//            public boolean isFinished() { return !robot.isDriveBusy()
//                    && !robot.isArmBusy()
//                    && !robot.isExtendBusy(); }
//            public String leave() {
//                robot.stopAllMotors();
//                return null;
//            }
//        });
//
//        robot.useSideCamera();
//
//        telemetry.addData("Ready...", null);
//        telemetry.update();
//
//        while (!isStarted())  {
//            MineralSample sample = robot.getMineralSample();
//            telemetry.addData("Gold:", sample.goldMineralLocations);
//            telemetry.addData("Silver:", sample.silverMineralLocations);
//            telemetry.addData("Gold Mineral From Lander:", sample.goldMineralArrangementFromLander);
//            telemetry.addData("Last Position Of Gold Mineral From Lander:", LastGoldPositionFromLander);
//            telemetry.update();
//
//            if (sample.goldMineralArrangementFromLander != MineralSample.GoldMineralArrangementFromLander.UNKNOWN) {
//                LastGoldPositionFromLander = sample.goldMineralArrangementFromLander;
//            }
//
//            GoldPositionFromLander = sample.goldMineralArrangementFromLander;
//        }
//
//        waitForStart();
//
//        Steps stepsRunner = new Steps(steps, this);
//        stepsRunner.runStepsMap();
//
//        // =========================================================================================
//
//        robot.stopAllMotors();
//    }
//}