package org.jLOAF.Tetris;



import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

import org.jLOAF.action.AtomicAction;
import org.jLOAF.action.ComplexAction;
import org.jLOAF.casebase.Case;
import org.jLOAF.casebase.CaseBase;
import org.jLOAF.inputs.Feature;
import org.jLOAF.inputs.complex.Matrix;



public class LogFile2CaseBase
{

    private String m_logFileName;
    private String m_outputFileName;
    private CaseBase m_casebase;


    public static void main(String[] args) {

        String inFile = "log.txt";
        String outFile = "tetris.cb";

        //we will create an instance of the class
        LogFile2CaseBase lf2cb = new LogFile2CaseBase(inFile, outFile);

        //parse the log file and create the case base
        try{
            lf2cb.parseLogFile();
        }catch(IOException e){
            System.err.println("Error parsing log file: " + e.getMessage());
            return;
        }

        System.out.println("Writing Cases to file...");
        //write the case base to a file
        try{
            lf2cb.writeCaseBaseFile();
        }catch(IOException e){
            System.err.println("Error writing to output file: " + e.getMessage());
        }
        System.out.println("Finished!");
    }


    public LogFile2CaseBase(String logFile, String caseBaseFile) {
        //check params
        if(logFile == null || caseBaseFile == null){
            throw new IllegalArgumentException("Null parameters given to constructor.");
        }

        //save the name of the files to read and write to
        this.m_logFileName = logFile;
        this.m_outputFileName = caseBaseFile;

        //initialize the CaseBase
        this.m_casebase= new CaseBase();
    }


    public void parseLogFile() throws IOException {
        //open the log file
        BufferedReader in = new BufferedReader(new FileReader(this.m_logFileName));

        String line = null;


        System.out.println("Reading log data...");
        //read in each line of the log file
        while ((line = in.readLine()) != null){
            if(line.equals("") || line.equals("\n")){
                continue;
            }
            //line.replaceAll("?", " ");
            Scanner s = new Scanner(line);
            s.useDelimiter("\\?");
            String board = s.next();
            String piece = s.next();
            int x = new Integer(s.next());
            int y = new Integer(s.next());
            int rot = new Integer(s.next());
            double score = new Double(s.next());


            ComplexAction tetrisact = new TetrisAction();
            AtomicAction acts = new AtomicAction("TetrisFeatures");
            Feature f_x = new Feature(x);
            Feature f_y = new Feature(y);
            Feature f_rot = new Feature(rot);
            Feature f_score = new Feature(score);
            acts.addFeature(f_x);
            acts.addFeature(f_y);
            acts.addFeature(f_rot);
            acts.addFeature(f_score);

            tetrisact.add(acts);


            TetrisInput ti = new TetrisInput();


            double[][] boardMat = new double[20][10];

            Scanner scn = new Scanner(board);
            scn.useDelimiter(",");
            int cnt = 0;
            while(scn.hasNext()){
                String currRow = scn.next();
                if(cnt > 3){
                    Scanner s2 = new Scanner(currRow);
                    s2.useDelimiter("-");
                    for(int ii=0;ii<10;ii++){
                        boardMat[cnt-4][ii] =  new Double(s2.next());
                    }
                }
                cnt++;
            }

            Matrix tb = new Matrix("TetrisBoard",boardMat);


            double[][] pieceMat = new double[4][4];
            for(int ii=0; ii< pieceMat.length; ii++){
                for(int jj=0; jj<pieceMat[0].length; jj++){
                    pieceMat[ii][jj] = 0.0;
                }
            }

            scn = new Scanner(piece);
            scn.useDelimiter("-");

            //all tetris pieces have 4 blocks
            String v1 = scn.next();
            pieceMat[new Integer(v1.substring(0, 1))][new Integer(v1.substring(1, 2))] = 1;
            String v2 = scn.next();
            pieceMat[new Integer(v2.substring(0, 1))][new Integer(v2.substring(1, 2))] = 1;
            String v3 = scn.next();
            pieceMat[new Integer(v3.substring(0, 1))][new Integer(v3.substring(1, 2))] = 1;
            String v4 = scn.next();
            pieceMat[new Integer(v4.substring(0, 1))][new Integer(v4.substring(1, 2))] =  1;


            Matrix tp = new Matrix("TetrisPiece", pieceMat);

            ti.add(tb);
            ti.add(tp);

            Case c = new Case(ti,tetrisact);

            this.m_casebase.add(c);
        }

        System.out.println("Finished reading log file.");
        System.out.println(this.m_casebase.getSize() + " Cases were extracted.");

        //close the file input stream
        in.close();
    }


    public void writeCaseBaseFile() throws IOException {

        CaseBase.save(this.m_casebase,this.m_outputFileName);

    }

}
