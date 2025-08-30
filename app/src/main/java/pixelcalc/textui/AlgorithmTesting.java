package pixelcalc.textui;

import static pixelcalc.algorithm.Pixel.Color.ANY;
import static pixelcalc.algorithm.Pixel.Color.ANYCOLOR;

import java.util.ArrayList;
import java.util.Scanner;

import pixelcalc.algorithm.Backdrop;
import pixelcalc.algorithm.Pixel;
import pixelcalc.algorithm.PlacementCalculator;

final class AlgorithmTesting {
    public static void main(String[] args) {

        PlacementCalculator calculator = new PlacementCalculator();

        Scanner input = new Scanner(System.in);
        Backdrop backdrop = new Backdrop();
        boolean alwaysPlaceColored = true;
        boolean printPerIteration = true;
        backdrop.printRectangular = false;

        String[] colors = {
                " _ _ _ _ _ _",
                "_ _ _ _ _ _ _",
                " _ _ _ _ _ _",
                "_ _ _ _ _ _ _",
                " _ W _ _ _ _",
                "W W G _ _ _ _",
                " p W W _ _ _",
                "g y W g _ _ _",
                " W W y p _ _",
                "g p W W W _ _",
                " y W P Y W _",
        };

        for (int y = 0; y < colors.length; y++) {
            if (y % 2 == 0) colors[y] = "." + colors[y].substring(0, 12);
            String[] rowList = colors[y].split(" ");
            for (int x = 0; x < rowList.length; x++) {
                if (y % 2 == 0 && x == 0) continue;
                backdrop.add(new Pixel(x, 10 - y, Pixel.Color.fromString(rowList[x])));
            }
        }
        ArrayList<Pixel> optimalPlacements = calculator.getOptimalPlacements(backdrop);
        backdrop.print();
        System.out.println();
        for (Pixel pixel : optimalPlacements) pixel.print();
        System.out.println();

        boolean solve = false;
        while (!backdrop.isFull()) {
            if (!solve) {
                int x = input.nextInt();
                int y = input.nextInt();
                input.nextLine();
                String color = input.nextLine();
                if (color.equalsIgnoreCase("solve")) solve = true;
                backdrop.add(new Pixel(x, y, Pixel.Color.fromString(color)));
            }
            if (solve) {
                Pixel placement = optimalPlacements.getFirst();
                backdrop.add(alwaysPlaceColored && placement.color == ANY ? new Pixel(placement, ANYCOLOR) : placement);
            }
            optimalPlacements = calculator.getOptimalPlacements(backdrop);
            if (!solve || printPerIteration) {
                System.out.println();
                System.out.println();
                backdrop.print();
                System.out.println();
                for (Pixel pixel : optimalPlacements) pixel.print();
            }
        }
        if (solve && !printPerIteration) {
            System.out.println();
            System.out.println();
            backdrop.print();
            System.out.println();
            for (Pixel pixel : optimalPlacements) pixel.print();
        }
        System.out.println(backdrop.mosaicCount + " mosaics");
    }

}
