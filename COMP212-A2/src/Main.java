//import java.util.Scanner;
//
//public class Main {
//    static public Scanner sc = new Scanner(System.in);
//    static public int algoIDParser(){
//        try{
//            int number = sc.nextInt();
//            if (number ==1 || number == 2)
//                return number;
//            else {
//                System.out.println("Sorry, invalid input.");
//                System.exit(1);
//            }
//        }
//        catch (Exception e){
//            System.out.println("Sorry, invalid input.");
//            System.exit(1);
//        }
//        return 0;
//    }
//    public static void main(String args[]){
//
//        System.out.println("Please choose a simulator for Leader Election Algorithm:");
//        System.out.println("1) LCR simulator\t 2) HS simulator");
//        int algoID;
//        do {
//            algoID = algoIDParser();
//        }while (algoID == 0);
//        if(algoID == 1){
//            LCR.LCRAlgorithm();
//        }
//        if(algoID == 2) {
//            HS.HSAlgorithm();
//        }
//
//
//    }
//}