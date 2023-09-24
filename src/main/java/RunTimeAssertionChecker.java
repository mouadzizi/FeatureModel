import entities.Noeud;

import java.util.Arrays;

public class RunTimeAssertionChecker {

    public static void checkPreAssert(Noeud noeud) {
        if (noeud.getMandatory()) {
            System.out.println("-----------------------------------------------------");
            System.out.println(noeud.getName() + " is mandatory so it must be true.");
        }
        if ("OR".equals(noeud.getRelationship())) {
            long trueNoeuds = Arrays.stream(noeud.getNoeuds()).filter(Noeud::isTrue).count();
            if (trueNoeuds == 0) {
                System.out.println("---------------------------------------------------");
                System.out.println(noeud.getName() + " relation is OR so at least one of its children must be true.");
            }
        }
        if ("XOR".equals(noeud.getRelationship())) {
            long trueNoeuds = Arrays.stream(noeud.getNoeuds()).filter(Noeud::isTrue).count();
            if (trueNoeuds != 1) {
                System.out.println("---------------------------------------------------");
                System.out.println(noeud.getName() + " relation is XOR so exactly one of its children must be true.");
            }
        }

        if (noeud.getNoeuds() == null || noeud.getNoeuds().length == 0) return;
        for (Noeud child : noeud.getNoeuds()) {
            checkPreAssert(child);
        }
    }

    public static boolean checkPostAssert(Noeud noeud) {

        if (noeud.getMandatory() && !noeud.isTrue()) {
            System.out.println("------------------------------------------------");
            System.out.println("------Post Condition catch, you didn't respect your pre-condition --------");
            System.out.println(noeud.getName() + " is mandatory so it must be true.");
            System.out.println("------your model combination is FALSE --------");
            return false;
        }

        if (noeud.isTrue()) {
            if ("OR".equals(noeud.getRelationship())) {
                long trueNoeuds = Arrays.stream(noeud.getNoeuds()).filter(Noeud::isTrue).count();
                if (trueNoeuds == 0) {
                    System.out.println("\n-----------------------------------");
                    System.out.println("------you didn't respect your pre-condition --------");
                    System.out.println(noeud.getName() + " the relation is OR so at least one of its children must be true.");
                    System.out.println("------your model combination is FALSE --------");
                    return false;
                }
            }
            if ("XOR".equals(noeud.getRelationship())) {
                long trueNoeuds = Arrays.stream(noeud.getNoeuds()).filter(Noeud::isTrue).count();
                if (trueNoeuds != 1) {
                    System.out.println("\n-----------------------------------");
                    System.out.println("------you didn't respect your pre-condition --------");
                    System.out.println(noeud.getName() + " the relation is XOR so exactly one of its children must be true.");
                    System.out.println("------your model combination is FALSE --------");
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



}
