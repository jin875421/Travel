package glue502.software.utils;

import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;

import java.util.Arrays;
import java.util.Collections;

public class Shape {

    /**
     * 根据坐标进行形状判断的函数
     *
     * @param mop 传入的坐标集合
     * @return String型返回这个形状是什么形状
     */
    public static String getShape(MatOfPoint mop) {
        double area = Imgproc.contourArea(mop);
        MatOfPoint2f mop2f = new MatOfPoint2f(mop.toArray());
        // 计算弧长
        double arcLength = Imgproc.arcLength(mop2f, true);
        // 以指定的精度近似多边形曲线
        MatOfPoint2f approxCurve = new MatOfPoint2f();
        Imgproc.approxPolyDP(mop2f, approxCurve, 0.03 * arcLength, true);
        int count = approxCurve.toArray().length;
        Point[] points = approxCurve.toArray();
        // 三角形判断
        if (count == 3) {
            // 三个顶点，返回结果是[[507 408]]
            Point a = points[0];
            Point b = points[1];
            Point c = points[2];
            // 三个顶点对应的角度 （单位：度）
            int angleA = calculatingAngle(b, c, a);
            int angleB = calculatingAngle(a, c, b);
            int angleC = calculatingAngle(a, b, c);
            // 最大角
            Integer[] numbers = {angleA, angleB, angleC};
            int angleMax = (int) Collections.max(Arrays.asList(numbers));
            // 若cosA>0 或 tanA>0（A为最大角），则为锐角三角形，84-96
            // cos函数是以弧度作为参数
            if (Math.cos(Math.toRadians(angleMax)) > 0.05)
                return "锐角三角形";
            else if (Math.cos(Math.toRadians(angleMax)) < -0.05)
                return "钝角三角形";
            else return "直角三角形";
        }
        // 四边形判断
        else if (count == 4) {
            // 四个顶点
            Point a = points[0];
            Point b = points[1];
            Point c = points[2];
            Point d = points[3];
            // 四个顶点对应的角度 （单位：度）
            int angleA = calculatingAngle(b, d, a);
            int angleB = calculatingAngle(a, c, b);
            int angleC = calculatingAngle(b, d, c);
            int angleD = calculatingAngle(a, c, d);
            // 直线ab边长 （单位：像素）
            int ab = calculatingDistance(a, b);
            // 直线bc边长 （单位：像素）
            int bc = calculatingDistance(b, c);
            // 最大角
            Integer[] numbers = {angleA, angleB, angleC, angleD};
            int angleMax = (int) Collections.max(Arrays.asList(numbers));
            if (Math.abs(ab - bc) < 6 && Math.cos(Math.toRadians(angleMax)) < -0.07) {
                return "菱形";
            } else if (Math.abs(ab - bc) < 5)
                return "正方形";
            else return "长方形";
        }
        // 五角星
        else if (count == 10)
            return "五角星";
        else {
            // 弧长和面积的比值
            // 筛选出圆形
            // 圆半径
            double r = arcLength / (2 * Math.PI);
            double pi = area / (r * r);
            if (Math.abs(pi - Math.PI) < 1.0)
                return "圆形";
            else if (area < 100) {
                // N边形
                return "五角星";
            } else {
                return null;
            }
        }
    }


    /**
     * 从三个坐标点中计算角度
     *
     * @param p1 点1
     * @param p2 点2
     * @param p0 交点
     * @return 角度
     */
    private static int calculatingAngle(Point p1, Point p2, Point p0) {
        double x1 = p1.x - p0.x;
        double y1 = p1.y - p0.y;
        double x2 = p2.x - p0.x;
        double y2 = p2.y - p0.y;
        double angle = (x1 * x2 + y1 * y2) / Math.sqrt((x1 * x1 + y1 * y1) * (x2 * x2 + y2 * y2));
        return (int) (Math.acos(angle) * 180 / Math.PI);
    }

    /**
     * 从已知道的两个点计算两点之间距离
     *
     * @param p0 点1
     * @param p1 点2
     * @return 距离
     */
    private static int calculatingDistance(Point p0, Point p1) {
        double x1 = p1.x - p0.x;
        double y1 = p1.y - p0.y;
        return (int) (Math.sqrt(x1 * x1 + y1 * y1));
    }
}
