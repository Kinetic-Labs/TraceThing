package com.github.kinetic.tracething.util;

import com.github.kinetic.tracething.dto.ProfilingData;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings("CallToPrintStackTrace")
public class HtmlReportGenerator {

    public static void generateReport() {
        try {
            String template = loadResource("/report-template.html");
            String styles = loadResource("/report-style.css");
            String tableRows = generateTableRows();

            String reportContent = template
                    .replace("${title}", "TraceThing")
                    .replace("${heading}", "TraceThing")
                    .replace("${styles}", styles)
                    .replace("${table_rows}", tableRows);

            try(PrintWriter writer = new PrintWriter(new FileWriter("profiling-report.html"))) {
                writer.println(reportContent);
            }
        } catch(IOException ioException) {
            ioException.printStackTrace();
        }
    }

    private static String generateTableRows() {
        return ProfilingData.getMethodStats().entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.comparingLong(ProfilingData.MethodStats::getTotalDuration).reversed()))
                .map(entry -> {
                    String method = entry.getKey();
                    ProfilingData.MethodStats stats = entry.getValue();
                    double totalTimeMs = stats.getTotalDuration() / 1_000_000.0;
                    double avgTimeMs = totalTimeMs / stats.getCount();
                    return String.format("<tr><td>%s</td><td>%d</td><td>%.3f</td><td>%.3f</td></tr>",
                            method, stats.getCount(), totalTimeMs, avgTimeMs);
                })
                .collect(Collectors.joining("\n"));
    }

    private static String loadResource(String path) throws IOException {
        try(InputStream inputStream = HtmlReportGenerator.class.getResourceAsStream(path)) {
            if(inputStream == null)
                throw new IOException("Resource not found: " + path);

            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}