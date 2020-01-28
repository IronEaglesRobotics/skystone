//        package eaglesfe.roverruckus;
//
//        import com.qualcomm.robotcore.hardware.ColorSensor;
//        import  com.qualcomm.robotcore.hardware.DcMotor;
//        import com.qualcomm.robotcore.hardware.DcMotorSimple;
//        import com.qualcomm.robotcore.hardware.Servo;
//
//        import org.firstinspires.ftc.robotcore.external.Telemetry;
//
//        import javax.xml.transform.dom.DOMLocator;
//
//public class Arms {
//
//    private DcMotor Lift;
//    private DcMotor Collector;
//    private DcMotor Extend;
//    private Servo CollectorLeft;
//    private Servo CollectorRight;
//    private Servo sensorStick;
//    private ColorSensor sample;
//    boolean leftLast;
//    boolean rightLast;
//
//
//    public Arms(DcMotor Lift, DcMotor Collector, DcMotor Extend, Servo CollectorLeft, Servo CollectorRight,
//                Servo sensorStick, ColorSensor sample) {
//        this.Lift = Lift;
//        this.Collector = Collector;
//        this.Extend = Extend;
//        this.CollectorLeft = CollectorLeft;
//        this.CollectorRight = CollectorRight;
//        this.sensorStick = sensorStick;
//        this.sample = sample;
//
//        this.Lift.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
//        this.Collector.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
//        this.Extend.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
//
//        this.Lift.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//        this.Collector.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//        this.Extend.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//
//        this.CollectorLeft.setDirection(Servo.Direction.REVERSE);
//        this.CollectorRight.setDirection(Servo.Direction.FORWARD);
//        this.sensorStick.setDirection(Servo.Direction.FORWARD);
//
//        this.CollectorLeft.scaleRange(0, .75);
//        this.CollectorRight.scaleRange(0, .75);
//        this.sensorStick.scaleRange(0,1);
//
//        this.CollectorLeft.setPosition(1);
//        this.CollectorRight.setPosition(1);
//        this.sensorStick.setPosition(0);
//
//        this.sample.enableLed(true);
//    }
//
//    public void updateArms(float liftUp, float liftDown, float extendOut, float extendIn) {
//
//        Lift.setPower(liftUp - liftDown);
//        Extend.setPower(extendOut - extendIn);
//
//    }
//
//    public void updateArmsTime(double liftUp, float liftDown, float extendOut, float extendIn, double collectorUp, long millis, long tStart) {
//
//        while (System.currentTimeMillis() - tStart <= millis) {
//            Lift.setPower(liftUp - liftDown);
//            Extend.setPower(extendOut - extendIn);
//            Collector.setPower(collectorUp);
//        }
//
//        Lift.setPower(0);
//        Extend.setPower(0);
//        Collector.setPower(0);
//    }
//
//    public void updateCollector(float collectorUp, boolean collectorLeft, boolean collectorRight) {
//
//        boolean isLeftOpen = CollectorLeft.getPosition() > 0.5;
//        boolean isRightOpen = CollectorRight.getPosition() > 0.5;
//
//        if (collectorLeft && !leftLast) {
//            CollectorLeft.setPosition(isLeftOpen ? 0 : 1);
//        }
//
//        if (collectorRight && !rightLast) {
//            CollectorRight.setPosition(isRightOpen ? 0 : 1);
//        }
//        Collector.setPower(collectorUp * -1);
//
//        this.leftLast = collectorLeft;
//        this.rightLast = collectorRight;
//    }
//
//    public double getCollectorLeft() {
//        return CollectorLeft.getPosition();
//    }
//
//    public double getCollectorRight() {
//        return CollectorRight.getPosition();
//    }
//
//    public String getSample(){
//        return String.format("%d %d %d", sample.red(), sample.green(), sample.blue());
//    }
//}