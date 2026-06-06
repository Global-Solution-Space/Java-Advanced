package fiap.com.br.terranova.integration;

public final class FeignErrorUtil {

    private FeignErrorUtil() {
    }

    public static String extractDetail(String json) {
        try {
            if (json != null && json.contains("\"detail\":\"")) {
                int start = json.indexOf("\"detail\":\"") + 10;
                int end = json.indexOf("\"", start);
                return json.substring(start, end);
            }
        } catch (Exception ignored) {
        }
        return "Coordenadas ou parametros invalidos.";
    }
}
