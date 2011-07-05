package com.dborisenko.math.optimization.problems.parsers;

import com.dborisenko.math.optimization.problems.SetPackingProblem;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;
import org.apache.commons.math.linear.Array2DRowRealMatrix;
import org.apache.commons.math.linear.RealMatrix;

/**
 *
 * @author Denis Borisenko
 */
public class UnweightedSetPackingProblemParser {
    
    private RealMatrix constraints;

    protected Integer width;
    protected Integer height;

    private boolean firstLineFound = false;
    private int rowIndex = 0;

    /**
     * @return true, if line represents the first row of the matrix
     *          (i.e., the line begins from "p set").
     *         true, если строка представляет из себя первую строку матрицы
     *          (то есть, начинается с "p set").
     * @throws ParseException
     */
    protected boolean parseFirstLine(final String line, final int lineNumber)
            throws ParseException {
        String[] split = line.toLowerCase().split("\\s+");

        if (split.length == 4 && split[0].equals("p") && split[1].equals("set")) {
            try {
                width = Integer.parseInt(split[2]);
                height = Integer.parseInt(split[3]);

                constraints = new Array2DRowRealMatrix(height, width);
            }
            catch (NumberFormatException exception) {
                throw new ParseException(exception.getLocalizedMessage(), lineNumber);
            }
            return true;
        }
        return false;
    }

    /**
     * @return true, if line represents the row of the matrix (i.e., the line
     *          begins from litera "s").
     *         true, если строка представляет из себя строку матрицы
     *          (то есть, начинается с литеры "s").
     * @throws ParseException
     */
    protected boolean parseRowLine(final String line, 
            final int lineNumber,
            final int rowIndex)
            throws ParseException {

        if (rowIndex >= height) {
            throw new ParseException("Row index must be less then height",
                    lineNumber);
        }
        
        String[] split = line.toLowerCase().split("\\s+");

        if (split.length > 0 && split[0].equals("s")) {
            for (int i = 1; i < split.length; i++) {
                try {
                    int position = Integer.parseInt(split[i]) - 1;
                    if (position >= width || position < 0) {
                        throw new ParseException(
                                String.format("Position %s is out of bounds",
                                    position), lineNumber);
                    }
                    constraints.setEntry(rowIndex, position, 
                            SetPackingProblem.CONSTRAINT_COEFFICIENT_VALUE);
                }
                catch (NumberFormatException exception) {
                    throw new ParseException(exception.getLocalizedMessage(),
                            lineNumber);
                }
            }
            return true;
        }
        return false;
    }

    public SetPackingProblem getProblem() {
        return new SetPackingProblem(constraints);
    }

    public void parseLine(final String line, final int lineNumber)
            throws ParseException {
        if (!firstLineFound) {
            firstLineFound = parseFirstLine(line, lineNumber);
        } else {
            if (parseRowLine(line, lineNumber, rowIndex)) {
                ++rowIndex;
            }
        }
    }

    public void parse(String str)
            throws ParseException {
        String[] lines = str.split("\n");
        for (int i = 0; i < lines.length; i++) {
            parseLine(lines[i], i);
        }
    }

    public void read(BufferedReader reader)
            throws ParseException, IOException {
        String s;
        int lineNumber = 0;
        while((s = reader.readLine()) != null) {
            parseLine(s, lineNumber++);
        }
    }

    public void read(Reader file)
            throws ParseException, IOException {
        read(new BufferedReader(file));
    }

    public void readFromFile(String fileName)
            throws ParseException, IOException {
        read(new FileReader(fileName));
    }
    
}
