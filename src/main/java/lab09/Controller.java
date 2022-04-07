package lab09;

import javafx.fxml.FXML;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Controller {
    @FXML Canvas canvas;

    public void initialize() {
        List<Float> stockPrices1 = downloadStockPrices("AAPL");
        List<Float> stockPrices2 = downloadStockPrices("GOOG");
    
        drawLineChart(stockPrices1, stockPrices2, canvas);
    }

    private List<Float> downloadStockPrices(String ticker_symbol ) {
        List<Float> stockPrices = new ArrayList<>();

        System.out.println("ticker_symbol = " + ticker_symbol);

        long periodStart = LocalDate.of(2010, 1, 1).atStartOfDay(ZoneId.systemDefault()).toEpochSecond();
        System.out.println("periodStart = " + periodStart);
        long periodEnd = LocalDate.of(2015, 12, 31).atStartOfDay(ZoneId.systemDefault()).toEpochSecond();
        System.out.println("periodEnd = " + periodEnd);


        try {
            URL url = new URL("https://query1.finance.yahoo.com/v7/finance/download/" + ticker_symbol + "?period1=" + periodStart + "&period2="  + periodEnd + "&interval=1mo&events=history&includeAdjustedClose=true");

            URLConnection conn = url.openConnection();
            conn.setDoOutput(false);
            conn.setDoInput(true);
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String line = in.readLine();
            while((line = in.readLine()) != null) {
                String[] data = line.split(",");
                stockPrices.add(Float.parseFloat(data[4]));
            }

            in.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return stockPrices;
    }

    private void drawLineChart(List<Float> stockPrices1, List<Float> stockPrices2, Canvas canvas) {
        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        double padding = 50;
        double left = 0 + padding;
        double right = canvas.getWidth() - padding;
        double top = 0 + padding;
        double bottom = canvas.getHeight() - padding;

        gc.setStroke(Color.BLACK);
        gc.strokeLine(left, top, left, bottom);
        gc.strokeLine(left, bottom, right, bottom);

        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;

        for (int i = 0; i < stockPrices1.size(); i++) {
            if (stockPrices1.get(i) < min) {
                min = stockPrices1.get(i);
            }

            if (stockPrices1.get(i) > max) {
                max = stockPrices1.get(i);
            }
        }

        for (int i = 0; i < stockPrices2.size(); i++) {
            if (stockPrices2.get(i) < min) {
                min = stockPrices2.get(i);
            }

            if (stockPrices2.get(i) > max) {
                max = stockPrices2.get(i);
            }
        }
        
        plotLine(gc, stockPrices1, Color.BLUE, min, max, canvas);
        plotLine(gc, stockPrices2, Color.RED, min, max, canvas);
    }

    private void plotLine(GraphicsContext gc, List<Float> stockPrices, Color color, double min, double max, Canvas canvas) {
        double padding = 50;
        double left = 0 + padding;
        double bottom = canvas.getHeight() - padding;
        System.out.println("canvas.getHeight() = " + canvas.getHeight());

        int count = stockPrices.size();
        double xSpacing = (canvas.getWidth() - (2 * padding)) / count;
        double height = canvas.getHeight() - (2 * padding);

        gc.setStroke(color);
        double lastX = left;
        double lastY = bottom - (height * stockPrices.get(0) / max);

        for (int i = 1; i < stockPrices.size(); i++) {
            double y = bottom - (height * stockPrices.get(0) / max);
            gc.strokeLine(lastX, lastY, lastX + xSpacing, y);

            lastX += xSpacing;
            System.out.println("lastX = " + lastX);
            lastY = y;
            System.out.println("lastY = " + lastY);

        }
    }

}