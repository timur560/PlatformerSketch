package org.timur560.platformer.core;

import org.newdawn.slick.geom.Polygon;

/**
 * Created by timur on 28.01.15.
 */
public class Helper {
    public static int CELL_SIZE = 50;

    public static float[] cellsToPx(float x, float y) {
        return new float[]{x * 50, y * 50};
    }

    public static Polygon cellsToPolygon(float x1, float y1, float x2, float y2) {

        // 0, 25, 4, 25
        // (x1 == 0) ? 0 : x1*50 - 50, , 4*50, 25*50 - 50

        float[] floatArray = new float[]{
                x1 * 50,
                y1 * 50 - 50,
                x2 * 50,
                y1 * 50 - 50,
                x2 * 50,
                y2 * 50,
                x1 * 50,
                y2 * 50
        };

        return new Polygon(floatArray);
    }
}
