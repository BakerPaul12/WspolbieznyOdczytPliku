import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Main {
    public static void main(String[] args) {
        String inputFileName = "equations.txt";
        String outputFileName = "results.txt";

        try {

            BufferedReader br = new BufferedReader(new FileReader(inputFileName));
            BufferedWriter bw = new BufferedWriter(new FileWriter(outputFileName));

            int equationCount = 0;
            ExecutorService executorService = Executors.newFixedThreadPool(5);

            List<Future<Double>> results = new ArrayList<>();


            String line;
            while ((line = br.readLine()) != null) {

                equationCount++;


                Callable<Double> equationTask = new EquationTask(line);

                Future<Double> futureResult = executorService.submit(equationTask);
                results.add(futureResult);
            }

            for (int i = 0; i < results.size(); i++) {
                Future<Double> futureResult = results.get(i);
                String equation = br.readLine();

                try {
                    double result = futureResult.get();

                    // Zapisz wynik do pliku
                    bw.write("Equation " + (i + 1) + " "  + "=" + result);
                    bw.newLine();
                } catch (InterruptedException | ExecutionException e) {
                    System.err.println("Błąd obliczeń dla równania " + (i + 1) + ": " + e.getMessage());
                }
            }

            // Zamknij ExecutorService
            executorService.shutdown();

            br.close();
            bw.close();

            System.out.println("Zliczono i zapisano wyniki dla " + equationCount + " równań.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
