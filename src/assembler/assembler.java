/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package assembler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.Scanner;

public class assembler {

    public static boolean isNumeric(String str) { //function to check if string is a digit or not
        try {
            double d = Double.parseDouble(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public static void main(String args[]) throws FileNotFoundException, IOException {
        // Create a hash map
        Hashtable optable = new Hashtable(); //hashmap of operation code
        Hashtable symtable = new Hashtable(); //hashmap of symbol tabel (labels+LC)
        Hashtable literalvalue = new Hashtable();
        Hashtable literallength = new Hashtable();
        Hashtable literaladdress = new Hashtable();
        int LOCCOUNT1 = 0; //VARIABLE FOR DECIMAL LOCTION COUNTER 
        int STARTING1 = 0; //VARIABLE FOR starting address
        int prolength1 = 0; //VARIABLE FOR DECIMAL program length
        int flag = 0;  //indexed addressing mode flag
        String label1 = null; //VARIABLE string to save name of program
        int LEN = 0; //VARIABLE TO CALCULATE LENGTH OF RECORD 
        int p = 0; //INDEXOF TEXT RECORD LENGTH OF RECORD AND STARTING ARRAYS
        int lflag = 0;
        int cnt = 0;
        String lengthliteral = "0";
        String litralval = null;
        int o = 0; //INDEX OF LOCATION COUNTER ARRAY
        int n = 0; //ENTER END FLAG
        int pc1 = 0; //COUNTER OF PASS ONE AND ERROR ARRAY
        int we = 0; //ENTER START FLAG
        String[] error = new String[100]; //ERROR ARRAY
        String LOCCOUNT = null; //STRING TO READ LC HEXA
        String STARTING = null; //STRING TO READ START ADDRESS HEXA
        String[] lc = new String[100]; //LOCATION COUNTER ARRAY
        String[] litrals = new String[100];
        int S = 0;
        int f = 0, q = 0, cntlt = 0;
        //+++++OPTABLE INSETION+++++//
        optable.put("ADD", "18");
        optable.put("AND", "40");
        optable.put("COMP", "28");
        optable.put("DIV", "24");
        optable.put("J", "3C");
        optable.put("JEQ ", "30");
        optable.put("JGT ", "34");
        optable.put("JLT", "38");
        optable.put("JSUB", "48");
        optable.put("LDA", "00");
        optable.put("LDCH", "50");
        optable.put("LDL", "08");
        optable.put("LDX", "04");
        optable.put("MUL", "20");
        optable.put("OR", "44");
        optable.put("RD", "D8");
        optable.put("RSUB", "4C");
        optable.put("STA", "0C");
        optable.put("STCH", "54");
        optable.put("STL", "14");
        optable.put("STX ", "10");
        optable.put("SUB", "1C");
        optable.put("TD", "DC");
        optable.put("TIX", "2C");
        optable.put("WD", "E0");
        optable.put("add", "18");
        optable.put("and", "40");
        optable.put("comp", "28");
        optable.put("div", "24");
        optable.put("j", "3C");
        optable.put("jeq", "30");
        optable.put("jgt", "34");
        optable.put("jlt", "38");
        optable.put("jsub", "48");
        optable.put("lda", "00");
        optable.put("ldch", "50");
        optable.put("ldl", "08");
        optable.put("ldx", "04");
        optable.put("mul", "20");
        optable.put("or", "44");
        optable.put("rd", "D8");
        optable.put("rsub", "4C");
        optable.put("sta", "0C");
        optable.put("stch", "54");
        optable.put("stl", "14");
        optable.put("stx", "10");
        optable.put("sub", "1C");
        optable.put("td", "DC");
        optable.put("tix", "2C");
        optable.put("wd", "E0");
        //++++++pass one++++++++++//
        //**************************//
        try {

            String op = "null";
            String label;
            File file = new File("sourcefile.txt"); //DECLARE SCR FILE AND CREATE FILE INPUT TO READ
            FileInputStream input = new FileInputStream(file);
            Scanner reader = new Scanner(input);
            while (reader.hasNext()) { //LOOP TILL END OF FILE
                String buffer = reader.nextLine(); //BUFFER SAVES THE LINES OF FILE
                if (!buffer.startsWith(".")) { //IF LINE NOT A COMMENT
                    label = buffer.substring(0, Math.min(7, buffer.length()));
                    op = (buffer.substring(9, Math.min(14, buffer.length())));
                    String operand = buffer.substring(17, Math.min(34, buffer.length())); //READING WITH DESIRED FORMAT
                    String blank = buffer.substring(8, Math.min(9, buffer.length()));
                    String blank2 = buffer.substring(15, Math.min(17, buffer.length()));

                    op = op.replace(" ", ""); //REPLACE EACH SPACE TO NULL FOR INSERTION IN SYMTABLE AND READ FROM OP TABLE
                    operand = operand.replace(" ", "");
                    label = label.replace(" ", "");

                    if (operand.contains(",")) { //CHECK , TO INDECATE INDEXXED ADDRESSING

                        String parts[] = operand.split("\\,");
                        operand = parts[0];

                    }
                    if (operand.startsWith("=")) {
                        lflag = 1;

                        String parts[] = operand.split("\\=");
                        operand = parts[1];
                        if (symtable.get(operand) == null) {
                            cnt++;
                        }
                        litrals[S++] = parts[1];
                        if (parts[1].startsWith("x")) {
                            litralval = parts[1].substring(2, (parts[1].length() - 1));
                            literalvalue.put(parts[1], litralval);
                            literallength.put(parts[1], "3");
                        } else if (parts[1].startsWith("c")) {
                            char[] chars = parts[1].toCharArray();
                            StringBuffer hex = new StringBuffer();
                            for (int i = 3; i < (chars.length - 1); i++) {
                                hex.append(Integer.toHexString((int) chars[i]));
                            }
                            litralval = hex.toString();
                            lengthliteral = String.valueOf((litralval.length() / 2));
                            literalvalue.put(parts[1], litralval);
                            literallength.put(parts[1], lengthliteral);
                            op = "byte";
                        }

                    }
                    if (op.equals("START") || op.equals("start")) {

                        if (we == 1) {
                            error[pc1] = "***start is once in the program";
                        }
                        if (isNumeric(operand)) { //IF NUMBER OPERAND CONVERT IT TO DECIMAL TO GET LENGTH OF PROGRAM THEM BK HEXA AND INSERT IN lc array
                            LOCCOUNT1 = Integer.valueOf(String.valueOf(operand), 16);
                            STARTING1 = Integer.valueOf(String.valueOf(operand), 16);
                            label1 = label;
                            LOCCOUNT = Integer.toHexString(LOCCOUNT1);
                            STARTING = Integer.toHexString(STARTING1);
                            lc[o] = LOCCOUNT;
                            o++;
                        } else { //if not default is hexa address 1000 that is 4096 dec and add to array
                            error[pc1] = "***must be digit operand default starting address 1000";
                            LOCCOUNT1 = 4096;
                            STARTING1 = 4096;
                            label1 = label;
                            LOCCOUNT = Integer.toHexString(LOCCOUNT1);
                            STARTING = Integer.toHexString(STARTING1);
                            lc[o] = LOCCOUNT;
                            o++;
                        }
                        we = 1;

                        pc1++; //increment line counter
                    } else {
                        if (!label.startsWith(".")) { //calculate lc based on op code
                            if (optable.get(op) != null) {
                                LOCCOUNT1 = LOCCOUNT1 + 3;
                            } else if (op.equals("WORD") || op.equals("word")) {
                                if (!isNumeric(operand)) {
                                    error[pc1] = "***wrong operand format default operand zero";
                                    LOCCOUNT1 = LOCCOUNT1 + 3;
                                } else {
                                    LOCCOUNT1 = LOCCOUNT1 + 3;
                                }
                            } else if (op.equals("RESW") || op.equals("resw")) {
                                if (!isNumeric(operand)) {
                                    error[pc1] = "***wrong operand format";
                                } else {
                                    LOCCOUNT1 = LOCCOUNT1 + (3 * Integer.valueOf(operand));
                                }

                            } else if (op.equals("RESB") || op.equals("resb")) {
                                if (!isNumeric(operand)) {
                                    error[pc1] = "***wrong operand format";
                                } else {
                                    LOCCOUNT1 = LOCCOUNT1 + Integer.valueOf(operand);
                                }
                            } else if (op.equals("BYTE") || op.equals("byte")) {

                                if (isNumeric(operand)) {
                                    error[pc1] = "***wrong operand format";

                                    LOCCOUNT1 = LOCCOUNT1 + (operand.length() - 3);
                                } // as to cancel c'' and x''

                                LOCCOUNT1 = LOCCOUNT1 + (operand.length() - 3);

                            } else if (op.equals("ORG") || op.equals("org")) {

                                if (isNumeric(operand)) {
                                    LOCCOUNT1 = Integer.valueOf(String.valueOf(operand), 16);
                                } else {
                                    if (symtable.get(operand) != null) {
                                        LOCCOUNT1 = Integer.valueOf(String.valueOf(symtable.get(operand)), 16);
                                    } else {
                                        error[pc1] = "***wrong operand format not defined before org";
                                    }
                                }
                            } else if (op.equals("equ") || op.equals("EQU")) {
                                if (isNumeric(operand)) {
                                    symtable.put(label, lc[o - 1]);
                                } else {
                                    if (symtable.get(operand) != null) {
                                        symtable.put(label, symtable.get(operand));
                                    } else {
                                        String s = null;
                                        int kp = 1, lm = 0, pairflagr = 0;
                                        if (operand.contains("+") || (operand.contains("-"))) {
                                            char[] chars = operand.toCharArray();
                                            StringBuilder builder = new StringBuilder(5);
                                            ///////////////////////////////////////////////////////////////
                                            for (int i = 0; i < (chars.length); i++) {

                                                if (chars[i] == ('+') || chars[i] == '-') {
                                                    s =builder.append(chars[i]).toString();
                                                         System.out.println(s+chars[i]);
                                                    if (chars[i] == ('+')) {
                                                        kp++;
                                                    }
                                                    if (chars[i] == ('-')) {
                                                        lm++;
                                                    }
                                                }
                                            }
                                            System.out.println(s);
                                            chars = s.toCharArray();
                                            String news[] = new String[10];
                                            for (int i = 0; i < chars.length; i++) {
                                                if (chars[i] == '+') {
                                                    String parts[] = operand.split("\\+");
                                                    System.out.println(parts[i]+ i+s+"+");
                                                    String k = parts[i];
                                                    news[i] = k;

                                                    if (i == chars.length - 1) {
                                                        news[i + 1] = parts[1];
                                                    }
                                                }
                                                if (chars[i] == '-') {
                                                    String parts[] = operand.split("\\-");
                                                    System.out.println(parts[i]+ i+s+"-");
                                                    
                                                    news[i] = parts[0];
                                                    if (i == chars.length - 1) {
                                                        news[i + 1] = parts[1];
                                                    }
                                                }

                                            }
                                            if ((chars.length+1) % 2 == 0) {
                                                if (kp == lm) {
                                                    pairflagr = 1;
                                                }
                                            } else {
                                                if (kp == lm + 1) {
                                                    pairflagr = 1;
                                                }
                                            }
                                           System.out.println(kp+"vvv"+lm);
                                           int k=0;
                                            for (int i = 0; i < chars.length; i++) {
                                                  int relativeflag1 = 0, relativeflag2 = 0;
                                                  System.out.println(news[i]+"awel beta3"+news[i+1]);
                                                if (!isNumeric(news[i]) && pairflagr == 1) {
                                                    news[i] = (String) symtable.get(news[i]);
                                                    relativeflag1 = 1;
                                                }
                                                if (!isNumeric(news[i + 1]) && pairflagr == 1) {
                                                    news[i + 1] = (String) symtable.get(news[i + 1]);
                                                    relativeflag2 = 1;
                                                }
                                            
                                                  System.out.println(news[i]+"awel beta3"+news[i+1]);
                                              if((chars.length+1) % 2 != 0)
                                              { news[i+1]="0";
                                              
                                              relativeflag2 =relativeflag1;
                                             
                                              
                                              }
                                        
                                              
                                                if (chars[i] == '-'&& relativeflag1==relativeflag2) {
                                                  k =k+ Integer.valueOf(String.valueOf(news[i]), 16) - Integer.valueOf(String.valueOf(news[i + 1]), 16);
                                                  
                                                    symtable.put(label, Integer.toHexString(k));
                                                      System.out.println(Integer.toHexString(k)+"00---00"+news[i]+"999"+news[i+1]);
                                                }  System.out.println("ghhghg"+chars[i]+ relativeflag1+relativeflag2);
                                                if (chars[i] == '+'&& relativeflag1==relativeflag2) {
                                                     System.out.println("ghhghg");
                                                   k = k+Integer.valueOf(String.valueOf(news[i]), 16) + Integer.valueOf(String.valueOf(news[i + 1]), 16);
                                                    symtable.put(label, Integer.toHexString(k));
                                                      System.out.println(Integer.toHexString(k)+"7777"+news[i]+"00+++00"+news[i+1]);
                                                }
                                            
                                                   if(relativeflag1!=relativeflag2 )
                                                {error[pc1] = "***wrong operand format relative to relative or absoulte to";}
                                                
                                            }
                                            ////////////////////////////////////////////////////////////////////   
                                        } else {
                                            error[pc1] = "***wrong operand format";
                                        }

                                    }
                                }
                            } else if (op == null) {
                                error[pc1] = "***missing operation code";
                            } else if (op.equals("END") || op.equals("end")) {
                                if (n == 1) {
                                    error[pc1] = ""
                                            + "***WRONG format repeated end statement";
                                }
                                if (!(operand.equals(label1))) {

                                    if (!(operand.equals(lc[0]))) {
                                        error[pc1] = "***missing or misplaced or illegal operand";
                                    }
                                }

                                n = 1;
                            } else if (op.equals("LTORG") || op.equals("ltorg")) {
                                cntlt++;
                                lc[o] = Integer.toHexString(LOCCOUNT1);
                                o++;
                                for (f = q; f < cnt; f++) {
                                    if (symtable.get(litrals[f]) == null) {
                                        String fk = (String) literallength.get(litrals[f]);
                                        LOCCOUNT1 = LOCCOUNT1 + (Integer.valueOf(fk, 16));
                                        lc[o] = Integer.toHexString(LOCCOUNT1);
                                        literaladdress.put(litrals[f], lc[o - 1]);
                                        symtable.put(litrals[f], lc[o - 1]);
                                        o++;
                                    }

                                }
                                q = f;
                            } else {
                                error[pc1] = "***invalid or missing operation code";

                            }
                            LOCCOUNT = Integer.toHexString(LOCCOUNT1);
                            lc[o] = LOCCOUNT;
                            o++;
                            if (!blank.equals(" ")) {
                                error[pc1] = "***wrong formatof instruction";
                            }
                            if (!blank2.equals("  ")) {
                                error[pc1] = "***wrong formatof instruction";
                            }

                            if (!label.equals("") && !op.equals("equ") && !op.equals("EQU")) {
                                if (!isNumeric(label)) {
                                    if (symtable.get(label) == null) {
                                        symtable.put(label, lc[o - 2]);

                                    } else {
                                        error[pc1] = "***duplication symbol";

                                    }
                                } else {
                                    error[pc1] = "***illegal format in label field";
                                }
                            }

                            pc1++;
                        }

                    }
                }
            }
            if (n == 1 && lflag == 1) {
                for (f = q; f < cnt; f++) {
                    if (symtable.get(litrals[f]) == null) {
                        {
                            String fk = (String) literallength.get(litrals[f]);
                            LOCCOUNT1 = LOCCOUNT1 + (Integer.valueOf(fk, 16));
                            lc[o] = Integer.toHexString(LOCCOUNT1);
                            literaladdress.put(litrals[f], lc[o - 1]);
                            symtable.put(litrals[f], lc[o - 1]);
                        }
                        o++;
                    }

                }
            }

        } catch (IOException e) {
            System.out.println("error");
        }
        prolength1 = (Integer.valueOf(lc[o - 2], 16)) - STARTING1;
        String prolength = Integer.toHexString(prolength1);
        cnt = 0;
        f = 0;
        q = 0;
        cntlt = 0;
        //pass two
        //++++++++++++++++++++++++++++++++++++++++//
        //+++++++++++++++++++++++++++++++++++++++//

        String op = "null";
        File file = new File("sourcefile.txt");
        FileInputStream input = new FileInputStream(file);
        File file1 = new File("listfile.txt");
        BufferedWriter writer = null;
        writer = new BufferedWriter(new FileWriter(file1));
        File file2 = new File("objectcd.txt");
        BufferedWriter writer1 = null;
        String[] object = new String[100]; //array to hold objectcodes
        int s = 0; //index of objectcode array
        STARTING = String.format("%6s", STARTING).replace(' ', '0'); //replace the spaces in starting address by zeros
        int[] x = new int[100]; //array to save length of each text record
        String[] length = new String[100]; //array to save the following starting addresses of each text record
        writer1 = new BufferedWriter(new FileWriter(file2));
        writer1.write("H" + "^" + label1 + "^" + STARTING + "^" + prolength); //write header record
        writer1.newLine();

        Scanner reader = new Scanner(input);
        String objectcd;
        String label;
        o = 0;
        int pc2 = 0;
        while (reader.hasNext()) {
            String buffer = reader.nextLine();
            if (!buffer.startsWith(".")) {
                label = buffer.substring(0, Math.min(7, buffer.length()));
                op = (buffer.substring(9, Math.min(14, buffer.length())));
                String operand = buffer.substring(17, Math.min(34, buffer.length()));
                op = op.replace(" ", "");
                operand = operand.replace(" ", "");
                label = label.replace(" ", "");
                if (operand.contains(",")) {
                    flag = 1;
                    String parts[] = operand.split("\\,");
                    String operand1 = parts[0];
                    String k = (String) symtable.get(operand1);
                    int y = Integer.valueOf(k, 16) + 32768; //object code adds 8000 which is 32768 decimal when indexed
                    objectcd = optable.get(op) + Integer.toHexString(y);
                } else if (operand.contains("=")) {
                    String parts[] = operand.split("\\=");
                    operand = parts[1];
                    int calco = Integer.valueOf((String) (literaladdress.get(parts[1])), 16) - Integer.valueOf(lc[o + cntlt + 1], 16);
                    cnt++;
                    String k = (String) optable.get(op);
                    int y = Integer.valueOf(k, 16) + 3;
                    objectcd = Integer.toHexString(y) + "2" + String.format("%3s", Integer.toHexString(calco)).replace(' ', '0');;
                } else {
                    objectcd = optable.get(op) + String.valueOf(symtable.get(operand));
                }
                if (op.equals("RESW") || op.equals("RESB") || op.equals("END") || op.equals("START") || op.equals("org") || op.equals("equ") || op.equals("EQU") || op.equals("ORG") || op.equals("resw") || op.equals("resb") || op.equals("start") || op.equals("end")) {
                    objectcd = "      ";
                }
                if (op.equals("WORD") || op.equals("word")) {
                    if (!isNumeric(operand)) {
                        objectcd = "      ";

                    } else {
                        objectcd = Integer.toHexString(Integer.valueOf(operand));
                    }
                }
                if (op.equals("LTORG") || op.equals("ltorg")) {
                    objectcd = "     ";
                    cntlt++;
                }
                if (op.equals("BYTE") || op.equals("byte")) {
                    if (operand.startsWith("c")) {
                        char[] chars = operand.toCharArray();
                        StringBuffer hex = new StringBuffer();
                        for (int i = 3; i < (chars.length - 1); i++) {
                            hex.append(Integer.toHexString((int) chars[i]));
                        }
                        objectcd = hex.toString();
                    }
                    if (operand.startsWith("x")) {
                        objectcd = operand.substring(2, (operand.length() - 1));
                    }

                }

                String padded = String.format("%6s", objectcd).replace(' ', '0');
                writer.write(lc[o + cntlt] + " " + padded + " " + buffer);
                writer.newLine();

                if (op.equals("LTORG") || op.equals("ltorg")) {
                    for (f = q; f < cnt; f++) {
                        if (litrals[f] != null && symtable.get(litrals[f]).equals(lc[o + cntlt])) {
                            if (litrals[f].startsWith("x")) {
                                String wqw = (String) literalvalue.get(litrals[f]);
                                writer.write(lc[o + cntlt] + " 000000 " + litrals[f] + "        " + "WORD" + "    "
                                        + Integer.valueOf(String.valueOf(wqw), 16));
                                writer.newLine();
                            }
                            if (litrals[f].startsWith("c")) {
                                writer.write(lc[o + cntlt] + " 000000 " + litrals[f] + "        " + "BYTE" + "    " + literalvalue.get(litrals[f]));
                                writer.newLine();
                            }
                            o++;
                        }
                    }
                    q = f;
                }
                if (error[pc2] != null) {

                    writer.write(error[pc2]);
                    writer.newLine();
                }

                pc2++;

                String objectcd1 = objectcd.replace(" ", "");
                if (objectcd1 != null) {
                    object[s] = objectcd1;
                }
                LEN = object[s].length() + LEN;
                s++;
                if (LEN >= 52 || !reader.hasNext()) {
                    LEN = 0;
                    x[p] = (Integer.valueOf(String.valueOf(lc[o]), 16)) - STARTING1;
                    STARTING1 = (Integer.valueOf(String.valueOf(lc[o]), 16));
                    length[p] = Integer.toHexString(STARTING1);

                    p++;
                }
                if (!op.equals("START") && !op.equals("start")) {
                    o++;
                }
            } else {
                writer.write(buffer);
                writer.newLine();
            }

        }
        for (f = q; f < cnt; f++) {
            if (symtable.get(litrals[f]).equals(lc[o + cntlt])) {
                if (litrals[f].startsWith("x")) {
                    String wqw = (String) literalvalue.get(litrals[f]);

                    writer.write(lc[o++ + cntlt] + " 000000 " + litrals[f] + "        " + "WORD" + "    "
                            + Integer.valueOf(String.valueOf(wqw), 16));
                    writer.newLine();
                }
                if (litrals[f].startsWith("c")) {
                    writer.write(lc[o++ + cntlt] + " 000000 " + litrals[f] + "        " + "BYTE" + "    " + literalvalue.get(litrals[f]));
                    writer.newLine();
                }
            }

        }
        LEN = 0;
        p = 0;
        f = 0;
        q = 0;
        writer1.write("T" + "^" + STARTING + "^" + Integer.toHexString(x[0]));
        for (s = 0; s < object.length; s++) {

            if (object[s] != null) {
                writer1.write("^" + object[s]);
                LEN = object[s].length() + LEN;

                if (LEN >= 52) { //for following text records 
                    writer1.newLine();
                    LEN = 0;
                    p++;
                    writer1.write("T" + "^" + length[p] + "^" + Integer.toHexString(x[p]));
                }
            }
        }

        writer1.newLine();
        for (f = q; f < cnt; f++) {
            int kol = Integer.valueOf(String.valueOf(literaladdress.get(litrals[f])), 16) - Integer.valueOf(String.valueOf(lc[0]), 16);
            writer1.write("M" + "^" + Integer.toHexString(kol) + "^" + literallength.get(litrals[f]));
            writer1.newLine();
        }
        writer1.write("E" + "^" + STARTING);
        writer.close();
        writer1.close();

    }
}
