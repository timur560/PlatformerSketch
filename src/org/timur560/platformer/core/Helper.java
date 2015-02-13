package org.timur560.platformer.core;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Polygon;
import org.timur560.platformer.Platformer;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

    public static List<float[]> drops;
    public static void renderSnow(Graphics g, float[] offset) {
        if (drops == null) { // fill array
            int i;
            Random r = new Random();
            drops = new ArrayList<float[]>();

            for (i = 0; i <= 150; i++) {
                drops.add(new float[]{r.nextInt(Platformer.WIDTH), r.nextInt(Platformer.HEIGHT), r.nextInt(5)});
            }
        }

        // update positions
        for (float[] d : drops) {
            if (d[1] + ( d[2] / 5f ) > Platformer.HEIGHT) {
                d[1] = 0;
            } else {
                d[1] += ( d[2] / 5f );
            }
        }

        // draw
        g.setColor(new Color(1f,1f,1f,0.5f));
        for (float[] d : drops) {
            // g.fill(new Circle(d[0] + offset[0] / Platformer.ZOOM, d[1] + (offset[1] / Platformer.ZOOM), d[2]));
            g.fill(new Circle(d[0] + offset[0], d[1] + (offset[1]), d[2]));
        }
    }

    public static float[] offsetValues(int x, int y, float[] offset) {
//        return new float[]{x + offset[0] / Platformer.ZOOM, y + (offset[1] / Platformer.ZOOM)};
        return new float[]{x + offset[0], y + (offset[1])};
    }

    public static void renderRain(Graphics g, float[] offset, float zoom) {
        if (drops == null) { // fill array
            int i;
            Random r = new Random();
            drops = new ArrayList<float[]>();

            for (i = 0; i <= 300; i++) {
                drops.add(new float[]{r.nextInt(Platformer.WIDTH), r.nextInt(Platformer.HEIGHT), r.nextInt(8) + 5});
            }
        }

        // update positions
        for (float[] d : drops) {
            if (d[1] + ( d[2] / 5f ) > Platformer.HEIGHT) {
                d[1] = 0;
            } else {
                d[1] += ( d[2] / 5f ) + 5;
            }
        }

        // draw
        g.setColor(new Color(0.5f,0.5f,1f,0.5f));
        g.setLineWidth(2.0f);
        for (float[] d : drops) {
            g.drawLine(
                    d[0] + offset[0] / zoom,
                    d[1] + (offset[1] / zoom),
                    d[0] + offset[0] / zoom,
                    d[1] + (offset[1] / zoom) + d[2]);
        }

    }
}
