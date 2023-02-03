import com.fasterxml.jackson.databind.ObjectMapper;
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
    //Scanner object
    private static Scanner _scanner = new Scanner(System.in);

    public static void main(String[] args) throws IOException {

        // create an object mapper
        ObjectMapper mapper = new ObjectMapper();

        //ArrayList of all nodes
        ArrayList<String> nodes = new ArrayList<>();

        //Manual combination
        ArrayList<Boolean> combination  = new ArrayList<>();

        //String Boolean Expression
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

        System.out.println("what type of validation you want: ");
        System.out.println("1- Manual Validation at runtime");
        System.out.println("2- Contract & assertion validation at runtime");
        System.out.println("3- Dynamic validation");

        String choice = _scanner.nextLine();
        int choiceInt = Integer.parseInt(choice);

        System.out.print("Enter you constraint please: ");
        String constraint = _scanner.nextLine();
        generatedF+=" AND (" + constraint + ")";

        if (choiceInt == 1){
            System.out.println("You choosed manual Validation at runtime");
            System.out.println("Please enter all the values of each node in your Feature Model");
            nodes = findAllNodes(generatedF);
            combination = manualCombination(root, generatedF, nodes);
            BooleanExpression = trueBoolean(generatedF, nodes, combination);
            System.out.println("\n \n -----------------------------------");
            System.out.println("our Model as a boolean formula is :" + generatedF);
            VerificationManualRunTime(BooleanExpression);

        } else if (choiceInt == 2) {
            System.out.println("you choosed to check by Contract & assertion at runtime");
            RunTimeAssertionChecker.checkPreAssert(root.getNoeud());
            nodes = findAllNodes(generatedF);
            combination = manualCombination(root, generatedF, nodes);
            System.out.println("\n \n -----------------------------------");
            System.out.println("this is the formula before :" + generatedF);
            boolean check = RunTimeAssertionChecker.checkPostAssert(root.getNoeud());
            if (!check) {
                return;
            }else {
                System.out.println("Your Combination is Valid for your Feature Model");
            }
        }
        else{
            System.out.println("3- Dynamic validation");
        }

    }


    public static void VerificationManualRunTime(String BooleanExpression)
    {
        boolean result = RunTimeChecker.evaluateExpression(BooleanExpression);
        String formula = true ? "Valide" : "False";
        System.out.println("you Feature Model is : " + formula);
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
