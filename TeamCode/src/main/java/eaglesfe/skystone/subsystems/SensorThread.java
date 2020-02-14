package eaglesfe.skystone.subsystems;

import com.qualcomm.hardware.bosch.BNO055IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

public class SensorThread implements Runnable{

    private BNO055IMU imu;
    private GlobalAngle angle;
    Orientation lastAngles;

    public SensorThread(BNO055IMU imu, GlobalAngle angle) {
        this.imu = imu;
        this.angle = angle;
        this.lastAngles = new Orientation();
    }

    @Override
    public void  run() {

        Orientation angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        this.angle.globalAngle = angles.firstAngle;

        while (true) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            checkOrientation();
        }
    }

    public void checkOrientation() {

        Orientation angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);

        double deltaAngle = angles.firstAngle - lastAngles.firstAngle;
        this.angle.lastDelta = deltaAngle;

        if (deltaAngle < -180)
            deltaAngle += 360;
        else if (deltaAngle > 180)
            deltaAngle -= 360;

        this.angle.globalAngle += deltaAngle;

        lastAngles = angles;
    }
}
