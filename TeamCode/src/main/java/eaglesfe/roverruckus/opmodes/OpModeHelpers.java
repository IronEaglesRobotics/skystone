//package eaglesfe.roverruckus.opmodes;
//
//import android.graphics.Point;
//
//import com.eaglesfe.birdseye.FieldPosition;
//
//public class OpModeHelpers {
//
//    public static int getQuadrant(FieldPosition position) {
//        if (position.getY() > 0) {
//            return position.getX() > 0 ? 0 : 1;
//        }
//        return position.getX() > 0 ? 3 : 2;
//    }
//
//    public static Point getTurnaroundPointForAutonomous(FieldPosition robotPosition) {
//        int quadrant = OpModeHelpers.getQuadrant(robotPosition);
//        switch(quadrant) {
//            case 0:
//                return new Point(0,58);
//            case 1:
//                return new Point(-58,0);
//            case 2:
//                return new Point(0,-58);
//            case 3:
//                return new Point(58,0);
//        }
//        return new Point((int)robotPosition.getX(), (int)robotPosition.getY());
//    }
//}
