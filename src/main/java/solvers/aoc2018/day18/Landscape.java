package solvers.aoc2018.day18;

import java.util.List;

record Landscape(char[][] area, int width, int height) {
    private static final char BARREN = '.', FORESTED = '|', LUMBERYARD = '#';

    private static Landscape parse(List<String> input) {
        assert !input.isEmpty();
        var width = input.get(0).length();
        var height = input.size();
        var area = new char[width][height];
        for (int y = 0; y < height; y++) {
            var cs = input.get(y);
            for (int x = 0; x < width; x++) {
                area[x][y] = cs.charAt(x);
            }
        }
        return new Landscape(area, width, height);
    }

    private static Landscape from(Landscape landscape) {
        var width = landscape.width();
        var height = landscape.height;
        var area = new char[width][height];
        for (int x = 0; x < landscape.width(); x++) {
            System.arraycopy(landscape.area()[x], 0, area[x], 0, landscape.height());
        }
        return new Landscape(area, width, height);
    }

    @Override
    @SuppressWarnings("DuplicatedCode")
    public String toString() {
        var sb = new StringBuilder();
        sb.append(width).append(" x ").append(height).append(" landscape:\n");
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                sb.append(area[x][y]);
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
