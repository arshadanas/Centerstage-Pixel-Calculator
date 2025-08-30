package pixelcalc.algorithm;

/**
 * Note: this class has a natural ordering that is not consistent with equals.
 */
public final class Pixel implements Comparable<Pixel> {

    /**
     * @return The difference in {@link #scoreValue} between this {@link Pixel} and
     *         the provided {@link Pixel}
     */
    public int compareTo(Pixel other) {
        double diff = other.scoreValue - this.scoreValue;
        if (diff == 0)
            diff = this.y - other.y;
        if (diff == 0) {
            if (other.mHelper)
                diff++;
            if (this.mHelper)
                diff--;
        }
        return (int) (diff * 1000000000);
    }

    public final int x;
    public final int y;
    public final Color color;
    Color recommended = Color.EMPTY;
    double scoreValue = 0;
    public Pixel mosaic = null;
    boolean mHelper = false;

    public Pixel(int x, int y, Color color) {
        this.x = x;
        this.y = y;
        this.color = color;
    }

    /**
     * Instantiate a new {@link Pixel} object based on an existing {@link Pixel} but
     * with a new {@link Color}
     */
    public Pixel(Pixel p, Color color) {
        this(p.x, p.y, color);
        this.scoreValue = p.scoreValue;
        this.mHelper = p.mHelper;
        this.recommended = p.recommended;
    }

    /**
     * @return A copy of this {@link Pixel}
     */
    protected Pixel clone() {
        return new Pixel(this, color);
    }

    public boolean equals(Object o) {
        if (!(o instanceof Pixel p))
            return false;
        return this.equals(p);
    }

    public boolean equals(Pixel p) {
        return p.x == x && p.y == y;
    }

    /**
     * @return Whether this {@link Pixel} is part of a valid mosaic
     */
    public boolean inMosaic() {
        return mosaic != null && mosaic.color != Color.INVALID;
    }

    /**
     * @return Whether a {@link Pixel} with identical {@link #x} and {@link #y} is
     *         present in the provided {@link Iterable}
     */
    boolean isIn(Iterable<Pixel> array) {
        return getCounterpartIn(array) != null;
    }

    /**
     * @return The first {@link Pixel} with identical {@link #x} and {@link #y}
     *         present in the provided {@link Iterable} <br>
     *         Returns null if no such {@link Pixel} is found
     */
    Pixel getCounterpartIn(Iterable<Pixel> array) {
        for (Pixel p1 : array)
            if (this.equals(p1))
                return p1;
        return null;
    }

    /**
     * @return A {@link String} representation of this {@link Pixel}, including its
     *         {@link #x}, {@link #y}, and {@link #scoreValue} to 5 decimal places
     */
    public String toString() {
        double decPlaces = 100000;
        return "(%d, %d), %s%s, %s".formatted(x, y, recommended == Color.EMPTY ? "" : recommended.name() + " or ",
                color.name(), (int) (scoreValue * decPlaces) / decPlaces);
    }

    /**
     * Prints this scoring location to telemetry in an easily user-readable form
     */
    public String userFriendlyString() {
        return "%s, %s%s".formatted(userFriendlyX(), recommended == Color.EMPTY ? "" : recommended.name() + " or ",
                color.name());
    }

    public Color getColor() {
        return recommended != Color.EMPTY ? recommended : color;
    }

    private String userFriendlyX() {
        return switch (x) {
            case 0 -> "FAR LEFT";
            case 1 -> y % 2 == 0 ? "FAR LEFT" : "ALMOST FAR LEFT";
            case 2 -> y % 2 == 0 ? "ALMOST FAR LEFT" : "LEFT OF CENTER";
            case 3 -> y % 2 == 0 ? "CENTER LEFT" : "DEAD CENTER";
            case 4 -> y % 2 == 0 ? "CENTER RIGHT" : "RIGHT OF CENTER";
            case 5 -> "ALMOST FAR RIGHT";
            case 6 -> "FAR RIGHT";
            default -> "UNKNOWN";
        };
    }

    /**
     * Outputs the result of {@link #toString()} to the main text output stream
     */
    public void print() {
        System.out.println(this);
    }

    static boolean printInColor = true;

    /**
     * An enum representing the color of a given {@link Pixel},
     * either a real, physical color, or a placeholder in a {@link Backdrop}
     */
    public enum Color {
        PURPLE,
        YELLOW,
        GREEN,
        WHITE,
        EMPTY,
        ANY,
        ANYCOLOR,
        INVALID;

        private static final String RESET = "\u001B[0m";
        public static final Color[] colors = values();

        /**
         * @return A single-letter {@link String} representation of this {@link Color}
         *         <br>
         *         {@link #PURPLE}, {@link #GREEN}, and {@link #YELLOW} will have ANSI
         *         color codes if {@link #printInColor} is true
         */
        public String toString() {
            return switch (this) {
                case PURPLE -> (printInColor ? "" : "\u001B[35m") + "P" + RESET;
                case YELLOW -> (printInColor ? "" : "\u001B[33m") + "Y" + RESET;
                case GREEN -> (printInColor ? "" : "\u001B[32m") + "G" + RESET;
                case WHITE, ANY -> "" + name().charAt(0);
                case ANYCOLOR -> "C";
                case INVALID -> " ";
                default -> "_";
            };
        }

        public String humanInstruction() {
            return switch (this) {
                case WHITE, PURPLE, YELLOW, GREEN -> "" + (ordinal() + 1) % 4;
                default -> "";
            };
        }

        /**
         * @return The {@link Color} corresponding to a given {@link String}
         *         representation of what is (likely) originally a {@link Color}
         */
        public static Color fromString(String color) {
            return switch (color.toUpperCase()) {
                case "W" -> WHITE;
                case "#", "A" -> ANY;
                case "C" -> ANYCOLOR;
                case "P" -> PURPLE;
                case "Y" -> YELLOW;
                case "G" -> GREEN;
                case " " -> INVALID;
                default -> EMPTY;
            };
        }

        /**
         * @return Whether this {@link Color} "matches" the provided {@link Color},
         *         accounting for ambiguous and specific {@link Color}s
         */
        public boolean matches(Color other) {
            return (this != INVALID && other != INVALID) && (this == ANY ||
                    other == ANY ||
                    this == other ||
                    isColored() && other == ANYCOLOR ||
                    this == ANYCOLOR && other.isColored());
        }

        /**
         * @return Whether this {@link Color} is {@link #PURPLE}, {@link #GREEN}, or
         *         {@link #YELLOW}
         */
        boolean isColored() {
            return ordinal() <= 2;
        }

        /**
         * @return The first {@link Pixel} with identical {@link #color} present in the
         *         provided {@link Iterable} <br>
         *         Returns a {@link Pixel} with out-of-bounds coordinates if no matching
         *         {@link #color}ed {@link Pixel} is found
         */
        public Pixel getCounterpartIn(Iterable<Pixel> array, boolean isRed) {
            for (Pixel pixel : array)
                if (this.matches(pixel.color))
                    return pixel;
            return new Pixel((isRed ? -2 : 9), 0, EMPTY);
        }
    }
}
