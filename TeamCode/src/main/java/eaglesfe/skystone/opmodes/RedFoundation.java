//package eaglesfe.skystone.opmodes;
//
//import com.eaglesfe.birdseye.BirdseyeServer;
//import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
//import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import eaglesfe.common.Step;
//import eaglesfe.common.Steps;
//
//@Autonomous (name = "red foundation", group = "red competition")
//public class RedFoundation extends LinearOpMode{
//
//    @Override
//    public void runOpMode() {
//        final BirdseyeServer server = new BirdseyeServer(3708, telemetry);
//        final skystoneRobot robot = new skystoneRobot(hardwareMap);
//        robot.setVisionEnabled(true);
//        robot.useCameraTensor();
//
//        //map of the steps
//        Map<String, Step> steps = new HashMap<>();
//
//        steps.put("start", new Step("strafe to the left...") {
//            @Override
//            public void enter() {
//                robot.drive.setTargetStrafePositionRelative(-18, .4);
//            }
//
//            @Override
//            public boolean isFinished() {
//                return !robot.drive.isBusy();
//            }
//
//            @Override
//            public String leave() {
//                return "initial forward";
//            }
//        });
//
//        steps.put("initial forward", new Step("moving forward...") {
//            @Override
//            public void enter() {
//                robot.drive.setTargetPositionRelative(-28,.3);
//                robot.setArmPosition(.2,.3);
//
//            }
//
//            @Override
//            public boolean isFinished() {
//                return !robot.drive.isBusy();
//            }
//
//            @Override
//            public String leave() {
//                robot.foundationGrab(true);
//                return "grab foundation";
//            }
//        });
//
//        steps.put("grab foundation", new Step("repositioning foundation...") {
//            @Override
//            public void enter() {
//                robot.drive.setTargetPositionRelative(-3.5, .1);
//            }
//
//            @Override
//            public boolean isFinished() {
//                return !robot.drive.isBusy();
//            }
//
//            @Override
//            public String leave() {
//                return "corner";
//            }
//        });
//
//        steps.put("corner", new Step("corner...", 1400) {
//            @Override
//            public void enter() {
//                robot.drive.setInput(0,.4,0);
//                robot.setArmPosition(-.05,.3);
//            }
//
//            @Override
//            public boolean isFinished() {
//                return false;
//            }
//
//            @Override
//            public String leave() {
//                return "turn foundation";
//            }
//        });
//
//        steps.put("turn foundation", new Step("turning...", 2850) {
//            @Override
//            public void enter() {
//                robot.drive.setInput(0,0,0.3);
//            }
//
//            @Override
//            public boolean isFinished() {
//                return false;
//            }
//
//            @Override
//            public String leave() {
//                return "wall push";
//            }
//        });
//
//        steps.put("wall push", new Step("pushing...", 800) {
//            @Override
//            public void enter() {
//                robot.drive.setInput(0,-.2,0);
//            }
//
//            @Override
//            public boolean isFinished() {
//                return false;
//            }
//
//            @Override
//            public String leave() {
//                robot.foundationGrab(true);
//                return "park";
//            }
//        });
//
//        steps.put("park", new Step("parking...", 1200) {
//            @Override
//            public void enter() {
//                robot.drive.setInput(-1,1,0);
//            }
//
//            @Override
//            public boolean isFinished() {
//                return false;
//            }
//
//            @Override
//            public String leave() {
//                return "park two";
//            }
//        });
//
//        steps.put("park two", new Step("parking...", 600) {
//            @Override
//            public void enter() {
//                robot.drive.setInput(0,.5,0);
//            }
//
//            @Override
//            public boolean isFinished() {
//                return false;
//            }
//
//            @Override
//            public String leave() {
//                return "end";
//            }
//        });
//
//        steps.put("end", new Step("end") {
//            @Override
//            public void enter() {
//                robot.stopAllMotors();
//            }
//
//            @Override
//            public boolean isFinished() {
//                return !robot.drive.isBusy();
//            }
//
//            @Override
//            public String leave() {
//                return null;
//            }
//        });
//
//        //wait for the auto to be started
//        waitForStart();
//
//        //telemetry
//        telemetry.addData("intitialized", "");
//        telemetry.update();
//
//        //once started start running through the steps
//        Steps stepsRunner = new Steps(steps, this);
//        stepsRunner.runStepsMap();
//
//        /*================================================================================================*/
//
//        //end of autonomous
//        robot.stopAllMotors();
//    }
//}
