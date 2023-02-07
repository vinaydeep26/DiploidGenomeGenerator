
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;

public class Task {

    public static void main(String[] args) {

        ArrayList<String> referenceList1 = new ArrayList<>();
        ArrayList<String> referenceList2 = new ArrayList<>();
        ArrayList<String> chromosomeList = new ArrayList<>();
        ArrayList<Integer> chrPresentInVcf1 = new ArrayList<>();
        ArrayList<Integer> chrPresentInVcf2 = new ArrayList<>();
        ArrayList<String> outputList1 = new ArrayList<>();
        ArrayList<String> outputList2 = new ArrayList<>();

        StringBuilder referenceString = new StringBuilder();
	if (args.length < 4) {
            System.out.println("Usage: java Task <referencefilename> <vcfFilename> <Haplotype1Filename.fasta> <Haplotype2Filename.fasta>");
            System.exit(1);
        }
        String reffile = args[0];
	String vcffile = args[1];
	String output1 = args[2];
	String output2 = args[3];

        System.out.println("Starting at " + new Date());
        try {
            try (BufferedReader in = new BufferedReader(
                    new InputStreamReader(new FileInputStream(reffile), StandardCharsets.UTF_8))) {
                String row;
                while ((row = in.readLine()) != null) {
                    if (!row.contains(">")) {
                        referenceString.append(row);
                    } else {
                        chromosomeList.add(row.split(" ")[0].replace(">", ""));
                        chrPresentInVcf1.add(0);
                        chrPresentInVcf2.add(0);
                        if (referenceString.length() != 0) {
                            referenceList1.add(referenceString.toString());
                            referenceList2.add(referenceString.toString());
                            referenceString.setLength(0);
                        }
                    }
                }
            }
        } catch (final IOException e) {
            System.out.println(e);
        }

        referenceList1.add(referenceString.toString());
        referenceList2.add(referenceString.toString());
        System.out.println("\nreference file read successfully at " + new Date());

        int index1 = 0, differenceInLength1 = 0;
        int index2 = 0, differenceInLength2 = 0;
        StringBuilder outputString1 = new StringBuilder(), outputString2 = new StringBuilder();
        String prevChrVal1 = chromosomeList.get(index1), prevChrVal2 = chromosomeList.get(index2);

        try {
            try (BufferedReader in = new BufferedReader(
                    new InputStreamReader(new FileInputStream(vcffile), StandardCharsets.UTF_8))) {
                String row;
                while ((row = in.readLine()) != null) {
                    if (!row.startsWith("#")) {
                        String columns[] = row.split("\t");
                        if (columns.length == 10) {
                            if (!columns[9].startsWith("0")) {
                                String chrVal = columns[0];
                                index1 = chromosomeList.indexOf(chrVal);

                                if (index1 != -1) {
                                    if (!chrVal.equals(prevChrVal1)) {
                                        prevChrVal1 = chrVal;
                                        outputList1.add(outputString1.toString());
                                        differenceInLength1 = 0;
                                    }

                                    int position = (Integer.parseInt(columns[1]) - 1) + differenceInLength1;
                                    if (position <= referenceList1.get(index1).length()) {

                                        chrPresentInVcf1.set(index1, 1);
                                        outputString1.setLength(0);
                                        outputString1.append(referenceList1.get(index1).substring(0, (position)));

                                        String alt = columns[4];
                                        if (columns[4].contains(",")) {
                                            alt = columns[4].split(",")[Integer.parseInt(columns[9].substring(0, 1)) - 1];
                                        }

                                        int refLength = columns[3].length();
                                        int altLength = alt.length();
                                        differenceInLength1 += (altLength - refLength);
                                        outputString1.append(alt);
                                        outputString1.append(referenceList1.get(index1).substring((position
                                                + refLength), referenceList1.get(index1).length()));
                                        referenceList1.set(index1, outputString1.toString());
                                    }
                                }
                            }

                            String col92 = columns[9].substring(2, 3);
                            if (!col92.equals("0")) {
                                String chrVal = columns[0];
                                index2 = chromosomeList.indexOf(chrVal);

                                if (index2 != -1) {
                                    if (!chrVal.equals(prevChrVal2)) {
                                       prevChrVal2 = chrVal;
                                        outputList2.add(outputString2.toString());
                                        differenceInLength2 = 0;
                                    }
                                    int position = (Integer.parseInt(columns[1]) - 1) + differenceInLength2;
                                    if (position <= referenceList2.get(index2).length()) {

                                        chrPresentInVcf2.set(index2, 1);
                                        outputString2.setLength(0);
                                        outputString2.append(referenceList2.get(index2).substring(0, (position)));

                                        String alt = columns[4];
                                        if (columns[4].contains(",")) {
                                            alt = columns[4].split(",")[Integer.parseInt(col92) - 1];
                                        }

                                        int refLength = columns[3].length();
                                        int altLength = alt.length();
                                        differenceInLength2 += (altLength - refLength);
                                        outputString2.append(alt);
                                        outputString2.append(referenceList2.get(index2).substring((position
                                                + refLength), referenceList2.get(index2).length()));
                                        referenceList2.set(index2, outputString2.toString());
                                    }
                                }
                            }
                        }
                    }
                }
                if (outputString1.length() != 0) {
                    outputList1.add(outputString1.toString());
                }
                if (outputString2.length() != 0) {
                    outputList2.add(outputString2.toString());
                }
                System.out.println("arrayLists of output are ready at " + new Date());

                for (int i = 0; i < chrPresentInVcf1.size(); i++) {
                    if (chrPresentInVcf1.get(i) == 0) {
                        outputList1.add(referenceList1.get(i));
                    }
                }
                for (int i = 0; i < chrPresentInVcf2.size(); i++) {
                    if (chrPresentInVcf2.get(i) == 0) {
                        outputList2.add(referenceList2.get(i));
                    }
                }
                System.out.println("chromosome's not present in vcf file fixed at " + new Date());

                FileWriter filewriter = new FileWriter(new File(output1));

                for (int i = 0; i < chromosomeList.size(); i++) {
                    filewriter.write(">" + chromosomeList.get(i) + "\n");
                    int k = 100;
                    for (int j = 0; j < outputList1.get(i).length(); j += 100) {
                        if (k <= outputList1.get(i).length()) {
                            filewriter.write(outputList1.get(i).substring(j, k) + "\n");
                            k += 100;
                        } else {
                            filewriter.write(outputList1.get(i).substring(j, outputList1.get(i).length()) + "\n");
                        }
                    }
                }
                filewriter.close();
                System.out.println("output1 file ready at " + new Date());

                filewriter = new FileWriter(new File(output2));

                for (int i = 0; i < chromosomeList.size(); i++) {
                    filewriter.write(">" + chromosomeList.get(i) + "\n");
                    int k = 100;
                    for (int j = 0; j < outputList2.get(i).length(); j += 100) {
                        if (k <= outputList2.get(i).length()) {
                            filewriter.write(outputList2.get(i).substring(j, k) + "\n");
                            k += 100;
                        } else {
                            filewriter.write(outputList2.get(i).substring(j, outputList2.get(i).length()) + "\n");
                        }
                    }
                }
                filewriter.close();
                System.out.println("output2 file ready at " + new Date());
            }
        } catch (final IOException e) {
            System.out.println(e);
        }

    }
}
