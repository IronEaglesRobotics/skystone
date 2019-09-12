package eaglesfe.common;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import java.util.Map;

public class Steps {

    private Map<String, Step> steps;
    private LinearOpMode opMode;

    public Steps(Map<String, Step> steps, LinearOpMode opMode) {
        this.steps = steps;
        this.opMode = opMode;
    }

    public void runStepsMap() {
        Step step = steps.get("start");

        while (opMode.opModeIsActive()) {
            if (!step.isEnabled()) {
                step = steps.get(step.leave());
                continue;
            }

            long timeout = System.currentTimeMillis() + step.getTimeout();
            step.enter();

            opMode.telemetry.addData("STATE", step.getDescription());
            opMode.telemetry.update();

            while(step.isEnabled()
                    && !step.isFinished()
                    && opMode.opModeIsActive()
                    && System.currentTimeMillis() < timeout) {
                opMode.sleep(50);
            }

            step = steps.get(step.leave());

            if (step == null || !opMode.opModeIsActive()) {
                break;
            }
        }

    }
}
