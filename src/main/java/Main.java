import com.fasterxml.jackson.databind.ObjectMapper;
import entities.BooleanExpressionEvaluator;
import entities.Noeud;
import entities.Root;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    //the Objectif of the HashSet is to eliminate the duplicated data
    private static Set<String> generatedFormulas = new HashSet<>();
    //In order to check if the annotation is Boolean Formula, we created a String with all formula inside.
    private static String generatedF = "";

    private static Scanner _scanner = new Scanner(System.in);

    public static void main(String[] args) throws IOException {
        // create an object mapper
        ObjectMapper mapper = new ObjectMapper();
        //ArrayList of all nodes
        ArrayList<String> nodes = new ArrayList<>();
        //Manual combination
        ArrayList<Boolean> combination  = new ArrayList<>();
        //String
        String BooleanExpression = "";
        // read the JSON file and parse it into a Root object
        File jsonFile = new File("C:/Users/moadz/OneDrive/Documents/GitHub/FeatureModel/src/main/resources/ecommerce.json");
        // read the Root
        Root root = mapper.readValue(jsonFile, Root.class);
        //for the relationship XOR, OR and REQUIRED, we need to know the parent and childs, so i did set the parent first for each node
        setParent(root.getNoeud());
        //function of (dfs Depth-first search )
        dfs(root.getNoeud());
        //Add the last root to the formula
        generatedF+= "(" + root.getNoeud().getName() + " )";

        checkPreAssert(root.getNoeud());

        System.out.print("Enter you constraint please: ");

        //String input = _scanner.nextLine();
        //generatedF+=" AND (" + input + ")";

        nodes = findAllNodes(generatedF);
        combination = manualCombination(root, generatedF, nodes);
        _scanner.close();
        boolean check = checkPostAssert(root.getNoeud());
        if (!check) return;

        BooleanExpression = trueBoolean(generatedF, nodes, combination);
        System.out.println("\n \n -----------------------------------");
        System.out.println("this is the formula before :" + generatedF);
        System.out.println("\n \n -----------------------------------");
        System.out.println("this is the formula after :" + BooleanExpression);
        VerificationManualRunTime(BooleanExpression);
    }

    private static void checkPreAssert(Noeud noeud) {
        if (noeud.getMandatory()) {
            System.out.println("\n-----------------------------------");
            System.out.println(noeud.getName() + " is mandatory so it must be true.");
        }
        if ("OR".equals(noeud.getRelationship())) {
            long trueNoeuds = Arrays.stream(noeud.getNoeuds()).filter(Noeud::isTrue).count();
            if (trueNoeuds == 0) {
                System.out.println("\n-----------------------------------");
                System.out.println(noeud.getName() + " relationship = OR so at least one of his childs should be true.");
            }
        }
        if ("XOR".equals(noeud.getRelationship())) {
            long trueNoeuds = Arrays.stream(noeud.getNoeuds()).filter(Noeud::isTrue).count();
            if (trueNoeuds != 1) {
                System.out.println("\n-----------------------------------");
                System.out.println(noeud.getName() + " relationship = XOR so just one of his childs should be true.");
            }
        }

        if (noeud.getNoeuds() == null || noeud.getNoeuds().length == 0) return;
        for (Noeud child : noeud.getNoeuds()) {
            checkPreAssert(child);
        }

    }

    private static boolean checkPostAssert(Noeud noeud) {
        if (noeud.getMandatory() && !noeud.isTrue()) {
            System.out.println("\n-----------------------------------");
            System.out.println(noeud.getName() + " is mandatory so it must be true.");
            return false;
        }
        if (noeud.isTrue()) {
            if ("OR".equals(noeud.getRelationship())) {
                long trueNoeuds = Arrays.stream(noeud.getNoeuds()).filter(Noeud::isTrue).count();
                if (trueNoeuds == 0) {
                    System.out.println("\n-----------------------------------");
                    System.out.println(noeud.getName() + " relationship = OR so at least one of his childs should be true.");
                    return false;
                }
            }
            if ("XOR".equals(noeud.getRelationship())) {
                long trueNoeuds = Arrays.stream(noeud.getNoeuds()).filter(Noeud::isTrue).count();
                if (trueNoeuds != 1) {
                    System.out.println("\n-----------------------------------");
                    System.out.println(noeud.getName() + " relationship = XOR so just one of his childs should be true.");
                    return false;
                }
            }
        }

        if (noeud.getNoeuds() == null || noeud.getNoeuds().length == 0) return true;
        for (Noeud child : noeud.getNoeuds()) {
            boolean check = checkPostAssert(child);
            if(!check) return false;
        }
        return true;
    }

    public static void VerificationManualRunTime(String BooleanExpression)
    {
        boolean result = BooleanExpressionEvaluator.evaluateExpression(BooleanExpression);
        System.out.println("Expression is valid: " + result);
    }

    public static String trueBoolean(String formula, ArrayList<String> nodes, ArrayList<Boolean> booleanombination){
        for (int i = 0; i < nodes.size(); i++) {
            formula = formula.replaceAll(nodes.get(i), booleanombination.get(i) ? "True" : "False");
        }
        return formula;
    }

    public static ArrayList<String> findAllNodes(String formula){
        ArrayList<String> variables = new ArrayList<>();
        // Regular expression to match variables in the formula
        String regex = "(?i)\\b(?!OR|AND|NOT)[A-Za-z_]+\\b";
        // Compile the regular expression
        Pattern pattern = Pattern.compile(regex);
        // Match the regular expression against the formula
        Matcher matcher = pattern.matcher(formula);
        // Iterate through the matches and add them to the variables list
        while (matcher.find()) {
            String variable = matcher.group();
            if (!variables.contains(variable)) {
                variables.add(variable);
            }
        }
        return variables;
    }

    public static ArrayList<Boolean> manualCombination (Root root, String formula, ArrayList<String> nodes){
        ArrayList<Boolean> Booleanvariables = new ArrayList<>();
        for (String variableName : nodes) {
            System.out.println("Please give me the " + variableName + "(true or false) : ");
            String userInputString = _scanner.nextLine();
            Noeud noeud = root.getNoeudByName(root.getNoeud(), variableName);
            if (userInputString.equalsIgnoreCase("true") || userInputString.equalsIgnoreCase("false")) {
                Boolean userInput = Boolean.parseBoolean(userInputString);
                Booleanvariables.add(userInput);
                noeud.setTrue(userInput);
            } else {
                Booleanvariables.add(false);
                noeud.setTrue(false);
            }
        }
        return Booleanvariables;
    }


    public static void setParent(Noeud noeud) {
        if(noeud.getNoeuds() == null) return;
        for (Noeud fils : noeud.getNoeuds()) {
            if (fils.getFather() == null) {
                fils.setFather(noeud);
                setParent(fils);
            }
        }
    }


    public static void dfs(Noeud noeud) {
        noeud.setVisited(true);
        if (noeud.getFather() != null) {
            if(noeud.getMandatory()){
                String formula = printOMandatoryExpression(noeud);
                if (generatedFormulas.add(formula)) {
                    generatedF+= formula ;
                    System.out.println(formula);
                }
            }
            if ("OR".equals(noeud.getFather().getRelationship())) {
                String formula = printOrExpression(noeud);
                if (generatedFormulas.add(formula)) {
                    generatedF+= formula ;
                    System.out.println(formula);
                }
            } else if ("XOR".equals(noeud.getFather().getRelationship())) {
                String formula = printXorExpression(noeud);
                if (generatedFormulas.add(formula)) {
                    generatedF+= formula ;
                    System.out.println(formula);
                }
            } else {
                String formula = "("  + noeud.getName() +" -> "+ noeud.getFather().getName()+ ") AND ";
                if (generatedFormulas.add(formula)) {
                    generatedF+= formula ;
                    System.out.println(formula);
                }
            }
        }

        if(noeud.getNoeuds() == null) return;
        for (Noeud neighbor : noeud.getNoeuds()) {
            if (!neighbor.isVisited()) {
                dfs(neighbor);
            }
        }
    }

    public static String printOMandatoryExpression(Noeud noeud) {
        String result = "(" +  noeud.getFather().getName() + " -> " + noeud.getName() + ") AND "  ;
        return result;
    }

    public static String printOrExpression(Noeud noeud) {
        String result =  "(" + noeud.getFather().getName() +" -> (";
        int i = 0;
        for (i = 0; i < noeud.getFather().getNoeuds().length - 1; i++) {
            result += noeud.getFather().getNoeuds()[i].getName() + " OR ";
        }
        result += noeud.getFather().getNoeuds()[i].getName() +")) AND ";
        return result  ;
    }

    public static String printXorExpression(Noeud noeud) {
        //to print XOR relationship p-> 1 to 1 (g..gn) p->[(g1 AND NOTg2 AND ... NOTgn) OR .. ]
        String result = "(" +  noeud.getFather().getName() +" -> (";
        int max = noeud.getFather().getNoeuds().length -1 ;
        for (int i = 0; i < max+1; i++) {
            result += " ( " + noeud.getFather().getNoeuds()[i].getName() + " ";
            for (int j = 0; j < noeud.getFather().getNoeuds().length ; j++) {
                if(i!=j)
                    result += "AND (NOT " + noeud.getFather().getNoeuds()[j].getName() + ")";
            }
            if (i==max){
                result += ") ";
            } else result += ") OR";
        }
        result+= "))";
        return result + " AND ";
    }

}
