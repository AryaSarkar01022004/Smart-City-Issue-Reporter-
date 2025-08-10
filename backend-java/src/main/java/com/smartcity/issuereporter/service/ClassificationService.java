package com.smartcity.issuereporter.service;

import com.smartcity.issuereporter.config.AppProperties;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

@Service
public class ClassificationService {
  private final AppProperties props;
  private final HttpClient http = HttpClient.newHttpClient();

  public ClassificationService(AppProperties props) {
    this.props = props;
  }

  public static class Result {
    public String category;
    public double confidence;
    public String provider;
    public boolean autoCategorized = true;
  }

  public Result classify(String title, String description, byte[] imageBytes) {
    // Try ML API if configured (expects JSON response with category, confidence)
    if (props.getMl().getUrl() != null && !props.getMl().getUrl().isBlank()) {
      try {
        String boundary = "----sci" + System.currentTimeMillis();
        var body = new java.io.ByteArrayOutputStream();
        var crlf = "\r\n";
        // text fields
        body.write(("--" + boundary + crlf).getBytes());
        body.write(("Content-Disposition: form-data; name=\"title\"" + crlf + crlf).getBytes());
        body.write((title != null ? title : "").getBytes(StandardCharsets.UTF_8));
        body.write((crlf + "--" + boundary + crlf).getBytes());
        body.write(("Content-Disposition: form-data; name=\"description\"" + crlf + crlf).getBytes());
        body.write((description != null ? description : "").getBytes(StandardCharsets.UTF_8));
        // file
        body.write((crlf + "--" + boundary + crlf).getBytes());
        body.write(("Content-Disposition: form-data; name=\"image\"; filename=\"image.jpg\"" + crlf).getBytes());
        body.write(("Content-Type: application/octet-stream" + crlf + crlf).getBytes());
        body.write(imageBytes);
        body.write((crlf + "--" + boundary + "--" + crlf).getBytes());

        HttpRequest req = HttpRequest.newBuilder()
          .uri(URI.create(props.getMl().getUrl()))
          .timeout(java.time.Duration.ofMillis(props.getMl().getTimeoutMs()))
          .header("Content-Type", "multipart/form-data; boundary=" + boundary)
          .POST(HttpRequest.BodyPublishers.ofByteArray(body.toByteArray()))
          .build();
        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
          var json = new com.fasterxml.jackson.databind.ObjectMapper().readTree(resp.body());
          String cat = json.has("category") ? json.get("category").asText() : "other";
          double conf = json.has("confidence") ? json.get("confidence").asDouble() : 0.7;
          Result r = new Result();
          r.category = cat;
          r.confidence = conf;
          r.provider = "ml-api";
          return r;
        }
      } catch (Exception ignored) {}
    }

    // Fallback: keyword-based
    String t = ((title == null ? "" : title) + " " + (description == null ? "" : description)).toLowerCase(Locale.ROOT);
    Result r = new Result();
    r.provider = "keywords";
    if (containsAny(t, "pothole", "potholes", "crack", "asphalt", "damaged road")) { r.category = "pothole"; r.confidence = 0.9; return r; }
    if (containsAny(t, "garbage", "trash", "waste", "litter", "overflow")) { r.category = "garbage"; r.confidence = 0.9; return r; }
    if (containsAny(t, "streetlight", "street light", "lamp", "bulb", "dark street")) { r.category = "streetlight"; r.confidence = 0.85; return r; }
    if (containsAny(t, "waterlogging", "water-logging", "water logging", "flood", "stagnant", "sewage")) { r.category = "water-logging"; r.confidence = 0.8; return r; }
    r.category = "other"; r.confidence = 0.4;
    return r;
  }

  private boolean containsAny(String t, String... words) {
    for (String w : words) if (t.contains(w)) return true;
    return false;
  }
}
