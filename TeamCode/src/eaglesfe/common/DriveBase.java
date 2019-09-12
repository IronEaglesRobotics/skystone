package eaglesfe.common;

import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.matrices.VectorF;

public interface DriveBase {
    void setInputVector(VectorF vector);
    void setInput(double x, double y, double z);
    void setRunMode(DcMotor.RunMode runMode);
    void setBrakeMode(DcMotor.ZeroPowerBehavior brakeMode);
}
