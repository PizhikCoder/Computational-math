import core.SLAESolver;
import dataclasses.DataContainer;
import dataloaders.ConsoleDataLoader;
import dataloaders.DataLoader;
import dataloaders.FileDataLoader;
import exceptions.MatrixLoadingException;
import exceptions.InvalidDiagonalException;
import printers.ConsolePrinter;
import printers.Printer;

import java.io.*;
import java.util.Scanner;

public class Main {

    public static Printer printer;

    private static String getFilePath() {
        Scanner scanner = new Scanner(System.in);
        printer.println("Enter file path:");
        String filePath = "";
        do {
            File file = new File(scanner.nextLine());
            if (!file.exists()) {
                printer.println("File does not exist!");
                continue;
            }
            if (!file.canRead()) {
                printer.println("Can not access to the file!");
                continue;
            }
            filePath = file.getPath();
        } while (filePath.isEmpty());
        return filePath;
    }

    private static DataLoader selectLoader() {
        Scanner scanner = new Scanner(System.in);
        String choice;
        do {
            printer.println("Select data source (file/console)");
            choice = scanner.nextLine();
        } while (!choice.equals("file") && !choice.equals("console"));

        return switch (choice) {
            case "file" -> new FileDataLoader(getFilePath());
            default -> new ConsoleDataLoader();
        };
    }

    public static void main(String... args) {
        printer = new ConsolePrinter();
        DataLoader dataLoader = selectLoader();
        try{
            DataContainer dataContainer = dataLoader.load();
            SLAESolver slaeSolver = new SLAESolver(4);
            SLAESolver.Solution solution = slaeSolver.solve(dataContainer.matrix(), dataContainer.accuracy());
            printer.println(solution);
        } catch (MatrixLoadingException | InvalidDiagonalException e) {
            printer.printf("Error while executing: %s", e.getMessage());
        } catch (IOException e) {
            printer.println("Can not parse input file!");
        } catch (NumberFormatException e){
            printer.println("Can not parse input number!");
        }
    }
}
