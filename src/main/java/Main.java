import com.fasterxml.jackson.databind.ObjectMapper;
import entities.BooleanExpressionEvaluator;
import entities.Noeud;
import entities.Root;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    //the Objectif of the HashSet is to eliminate the duplicated data
    private static Set<String> generatedFormulas = new HashSet<>();
    //In order to check if the annotation is Boolean Formula, we created a String with all formula inside.
    private static String generatedF = "";

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
        File jsonFile = new File("C:/Users/Admin/IdeaProjects/FeatureModel/src/main/resources/tree.json");
        // read the Root
        Root root = mapper.readValue(jsonFile, Root.class);
        //for the relationship XOR, OR and REQUIRED, we need to know the parent and childs, so i did set the parent first for each node
        setParent(root.getNoeud());
        //function of (dfs Depth-first search )
        dfs(root.getNoeud());
        //Add the last root to the formula
        generatedF+="( " + root.getNoeud().getName() + " ) AND True";

        /*
        System.out.print("Enter you constraint please: ");
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        scanner.close();
        generatedF+=" AND (" + input + ")";
        scanner.close();
        */

        nodes = findAllNodes(generatedF);
        combination = manualCombination(generatedF, nodes);
        BooleanExpression = trueBoolean(generatedF, nodes, combination);
        System.out.println("\n \n -----------------------------------");
        System.out.println("this is the formula after :" + generatedF);
        System.out.println("\n \n -----------------------------------");
        System.out.println("this is the formula after :" + BooleanExpression);
        VerificationManualRunTime(BooleanExpression);
    }
    public static void VerificationManualRunTime(String BooleanExpression)
    {
        boolean result = BooleanExpressionEvaluator.evaluateExpression("((False -> (False OR False)) AND False) -> True");
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

    public static ArrayList<Boolean> manualCombination (String formula, ArrayList<String> nodes){
        Scanner scanner = new Scanner(System.in);
        ArrayList<Boolean> Booleanvariables = new ArrayList<>();
        for (String variableName : nodes) {
            System.out.println("Please give me the " + variableName + "(true or false) : ");
            String userInputString = scanner.nextLine();
            if (userInputString.equalsIgnoreCase("true") || userInputString.equalsIgnoreCase("false")) {
                Boolean userInput = Boolean.parseBoolean(userInputString);
                Booleanvariables.add(userInput);
            } else {
                Booleanvariables.add(false);
            }
        }
        scanner.close();
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
                    generatedF+=formula;
                    System.out.println(formula);
                }
            }
            if ("OR".equals(noeud.getFather().getRelationship())) {
                String formula = printOrExpression(noeud);
                if (generatedFormulas.add(formula)) {
                    generatedF+=formula;
                    System.out.println(formula);
                }
            } else if ("XOR".equals(noeud.getFather().getRelationship())) {
                String formula = printXorExpression(noeud);
                if (generatedFormulas.add(formula)) {
                    generatedF+=formula;
                    System.out.println(formula);
                }
            } else {
                String formula = noeud.getName() +" -> "+ noeud.getFather().getName()+ " AND ";
                if (generatedFormulas.add(formula)) {
                    generatedF+=formula;
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
        String result =  noeud.getFather().getName() +" -> " + noeud.getName() + " AND "  ;
        return result;
    }

    public static String printOrExpression(Noeud noeud) {
        System.out.println(noeud.getName() +" -> "+ noeud.getFather().getName()+ " AND ");
        String result = noeud.getFather().getName() +" -> (";
        int i = 0;
        for (i = 0; i < noeud.getFather().getNoeuds().length - 1; i++) {
            result += noeud.getFather().getNoeuds()[i].getName() + " OR ";
        }
        result += noeud.getFather().getNoeuds()[i].getName() +") AND ";
        return result;
    }

    public static String printXorExpression(Noeud noeud) {
        // to print child->parent
        System.out.println(noeud.getName() +" -> "+ noeud.getFather().getName()+ " AND");

        //to print XOR relationship p-> 1 to 1 (g..gn) p->[(g1 AND NOTg2 AND ... NOTgn) OR .. ]
        String result = noeud.getFather().getName() +" -> (";
        int max = noeud.getFather().getNoeuds().length -1 ;
        for (int i = 0; i < max+1; i++) {
            result += " ( " + noeud.getFather().getNoeuds()[i].getName() + " ";

            for (int j = 0; j < noeud.getFather().getNoeuds().length ; j++) {
                if(i!=j)
                    result += "AND (NOT " + noeud.getFather().getNoeuds()[j].getName() + ") ";

            }

            if (i==max){
                result += ") ";
            } else result += ") OR";
        }
        result += ")";
        return result;
    }

}
