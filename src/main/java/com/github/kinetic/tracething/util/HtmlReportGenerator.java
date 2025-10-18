package com.github.kinetic.tracething.util;

import com.github.kinetic.tracething.dto.ProfilingData;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Class to generate an HTML report from profiling data
 */
@SuppressWarnings("CallToPrintStackTrace")
public final class HtmlReportGenerator {

    public void generateReport() {
        try {
            final String template = new Resource("web", "report-template.html").read();
            final String styles = new Resource("web", "report-style.css").read();
            final String script = new Resource("web", "report-script.js").read();
            final String tableRows = generateTableRows();

            assert template != null;
            assert styles != null;
            assert script != null;

            final String reportContent = template
                    .replace("${title}", "TraceThing")
                    .replace("${heading}", "TraceThing")
                    .replace("${styles}", styles)
                    .replace("${script}", script)
                    .replace("${table_rows}", tableRows);

            try(final PrintWriter writer = new PrintWriter(new FileWriter("profiling-report.html"))) {
                writer.println(reportContent);
            }
        } catch(final IOException ioException) {
            ioException.printStackTrace();
        }
    }

    /**
     * Generate a table row for each method invocation
     *
     * @return the table rows
     */
    private String generateTableRows() {
        return ProfilingData.getMethodStats().entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.comparingLong(ProfilingData.MethodStats::getTotalDuration).reversed()))
                .map(entry -> {
                    final String method = entry.getKey();
                    final ProfilingData.MethodStats stats = entry.getValue();
                    final double totalTimeMs = stats.getTotalDuration() / 1_000_000.0;
                    final double avgTimeMs = totalTimeMs / stats.getCount();

                    return String.format("<tr><td>%s</td><td>%d</td><td>%.3f</td><td>%.3f</td></tr>",
                            method, stats.getCount(), totalTimeMs, avgTimeMs);
                })
                .collect(Collectors.joining("\n"));
    }
}