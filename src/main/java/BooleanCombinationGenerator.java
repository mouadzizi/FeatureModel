import java.util.ArrayList;
import java.util.List;

public class BooleanCombinationGenerator {

    public static ArrayList<ArrayList<Boolean>> generateCombinations(ArrayList<String> nodes) {
        ArrayList<ArrayList<Boolean>> result = new ArrayList<>();
        generateCombinations(nodes, 0, new ArrayList<Boolean>(), result);
        return result;
    }

    private static void generateCombinations(ArrayList<String> nodes, int index, ArrayList<Boolean> current, ArrayList<ArrayList<Boolean>> result) {
        if (index == nodes.size()) {
            result.add(new ArrayList<>(current));
            return;
        }

        current.add(false);
        generateCombinations(nodes, index + 1, current, result);
        current.remove(current.size() - 1);

        current.add(true);
        generateCombinations(nodes, index + 1, current, result);
        current.remove(current.size() - 1);
    }

}
