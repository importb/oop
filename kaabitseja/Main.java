import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.SimpleDateFormat;
import java.util.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import org.json.JSONObject;

public class Main {
    public static List<String> edetabelid = new ArrayList<>();
    public static List<List<List<String>>> skoorid = new ArrayList<>();
    public static List<List<List<String>>> skooriühikud = new ArrayList<>();
    public static List<List<String>> osalejad = new ArrayList<>();

    /**
     * kaabitseb veebilehe data
     *
     * @param link - link mida kaabitseda
     * @return - HttpResponse koos dataga.
     * @throws Exception - errors.
     */
    public static HttpResponse<String> scrapeWebsite(String link) throws Exception {
        // client
        HttpClient client = HttpClient.newHttpClient();

        // req
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(link))
                .build();

        // response
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public static Map<String, String> readEnvFile(String kaust) {
        Map<String, String> envMap = new HashMap<>();
        BufferedReader reader = null;
        try {
            // Read the .env file
            reader = new BufferedReader(new FileReader(kaust + ".env"));
            String line;
            while ((line = reader.readLine()) != null) {
                // Split the line by "=" to extract key-value pairs
                String[] parts = line.split("=", 2);
                if (parts.length == 2) {
                    String key = parts[0].trim();
                    String value = parts[1].trim();
                    envMap.put(key, value);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading .env file: " + e.getMessage());
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                // Ignore
            }
        }
        return envMap;
    }

    public static void writeDataToDatabase(ArrayList<String> data, String kaust) throws SQLException, ClassNotFoundException {
        Map<String, String> envMap = readEnvFile(kaust);

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Class.forName("org.json.JSONObject");
            Connection connection = DriverManager.getConnection(
                    String.format("jdbc:mysql://%s:%s/%s", envMap.get("MYSQL_HOST"), envMap.get("MYSQL_PORT"), envMap.get("MYSQL_NAME")),
                    envMap.get("MYSQL_USERNAME"),
                    envMap.get("MYSQL_PASSWORD")
            );
            System.out.println("Connection õnnestus");

            Timestamp currentTime = new Timestamp(System.currentTimeMillis());

            // hangime edetabelite tähtajad
            Statement fetchStatement = connection.createStatement();
            ResultSet rs = fetchStatement.executeQuery("select * from edetabelid");
            ArrayList<String> relevantsedEdetabelid = new ArrayList<String>();
            while (rs.next()) {
                String edetabelNimi = rs.getString("edetabel_nimi");
                String deadlineString = rs.getString("deadline");
                Timestamp deadlineTimestamp = Timestamp.valueOf(deadlineString);

                // kui pole veel tähtajas
                if (currentTime.before(deadlineTimestamp)) {
                    relevantsedEdetabelid.add(edetabelNimi);
                }
            }

            System.out.println(relevantsedEdetabelid);
            StringBuilder päring = new StringBuilder("insert into data (edetabel_id, edetabel_nimi, osaleja, skoor, skoor2, aeg) values ");

            int addedCount = 0;

            for (int i = 0; i < data.size(); i++) {
                JSONObject jsonobject = new JSONObject(data.get(i));

                if (!relevantsedEdetabelid.contains(jsonobject.getString("edetabel_nimi"))) {
                    continue;
                }

                if (addedCount != 0) {
                    päring.append(", ");
                }

                // kui meil on ühe skooriga edetabel
                if (jsonobject.isNull("skoor2")) {
                    päring.append(String.format("(%d, \"%s\", \"%s\", \"%s\", NULL, \"%s\")",
                            jsonobject.getInt("edetabel_id"),
                            jsonobject.getString("edetabel_nimi"),
                            jsonobject.getString("osaleja"),
                            jsonobject.getString("skoor"),
                            jsonobject.getString("timestamp")
                    ));
                } else {
                    päring.append(String.format("(%d, \"%s\", \"%s\", \"%s\", \"%s\", \"%s\")",
                            jsonobject.getInt("edetabel_id"),
                            jsonobject.getString("edetabel_nimi"),
                            jsonobject.getString("osaleja"),
                            jsonobject.getString("skoor"),
                            jsonobject.getString("skoor2"),
                            jsonobject.getString("timestamp")
                    ));
                }
                addedCount++;
            }

            System.out.println(päring.toString());

            if (addedCount == 0) {
                System.out.println("Päring on tühi, not writing");
            } else {
                Statement statement = connection.createStatement();
                statement.executeUpdate(päring.toString());

                statement.close();
                connection.close();
                System.out.println("Päring õnnestus!");
            }
        } catch (SQLException e) {
            System.out.println("SQL exception!");
            e.printStackTrace();

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        HttpResponse<String> unparsedData = scrapeWebsite("https://www.phxc.ee");

        String kaust;
        if (args != null && args.length > 0) {
            kaust = args[0];
        } else {
            kaust = "./";
        }

        // timestamp
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date currentDate = new Date();
        String now = dateFormat.format(currentDate);

        // ava fail kirjutamiseks
        File fail = new File(kaust + "data_" + now + ".txt");
        FileWriter kirjutaja = new FileWriter(fail, true);

        // Leiame JSON data
        ArrayList<String> JSONdata = new ArrayList<>();

        // Leiame PHXC andmed
        String[] body = unparsedData.body().split("\n");
        ArrayList<String> edetabelNimed = new ArrayList<>();
        ArrayList<ArrayList<ArrayList<String>>> edetabeliteInfo = new ArrayList<>();
        ArrayList<ArrayList<String>> praeguneEdetabel = new ArrayList<>();

        for (int i = 0; i < body.length; i++) {
            String line = body[i].trim();

            // Edetabeli nimi
            if (line.contains("<h1>")) {
                if (!praeguneEdetabel.isEmpty()) edetabeliteInfo.add(praeguneEdetabel);
                edetabelNimed.add(line.substring(4, line.length() - 5));
                praeguneEdetabel = new ArrayList<>();
            }

            // Osaleja nimi ja tema skoorid.
            if (line.contains("<span class=\"pseudo\">")) {
                ArrayList<String> osaleja = new ArrayList<>();

                String osalejaNimi = line.substring(21, line.length() - 7);

                // Leiame skoor 1
                String skoor1 = "null";
                String skoor1ühik = "null";
                String line2 = body[i + 1].trim();
                if (line2.contains("<span class=\"skoor\">")) {
                    String skoor1String = line2
                            .replace("<span class=\"skoor\">", "")
                            .replace("<span class=\"komakoht\">", "")
                            .replace("</span>", "");


                    String[] skoorAndmed = skoor1String.split(" ");

                    if (skoor1String.contains("(")) {
                        skoorAndmed = new String[]{skoor1String};
                    }

                    skoor1 = skoorAndmed[0];
                    if (skoorAndmed.length == 2) skoor1ühik = skoorAndmed[1];
                }

                // Leiame skoor 2
                String skoor2 = "null";
                String skoor2ühik = "null";
                String line3 = body[i + 2].trim();
                if (line3.contains("<span class=\"skoor\">")) {
                    String skoor1String = line3
                            .replace("<span class=\"skoor\">", "")
                            .replace("<span class=\"komakoht\">", "")
                            .replace("</span>", "");

                    String[] skoorAndmed = skoor1String.split(" ");

                    if (skoor1String.contains("(")) {
                        skoorAndmed = new String[]{skoor1String};
                    }

                    skoor2 = skoorAndmed[0];
                    if (skoorAndmed.length == 2) skoor2ühik = skoorAndmed[1];
                }

                // Lisa
                osaleja.add(osalejaNimi);
                osaleja.add(skoor1);
                osaleja.add(skoor1ühik);
                osaleja.add(skoor2);
                osaleja.add(skoor2ühik);

                praeguneEdetabel.add(osaleja);
            }
        }

        // lisame viimase edetabeli ka
        edetabeliteInfo.add(praeguneEdetabel);
        
        int index = 0;
        for (ArrayList<ArrayList<String>> edetabeliInfo : edetabeliteInfo) {

            for (ArrayList<String> info : edetabeliInfo) {
                String edetabeliNimi = edetabelNimed.get(index);
                String[] osalejaInfo = info.get(0).split(" ");

                if (osalejaInfo[0].contains("Juh")) continue;

                String osalejaNimi = osalejaInfo[1].replace("amp;", "");
                String skoor1 = info.get(1);
                String skooriÜhik1 = info.get(2);
                String skoor2 = info.get(3);
                String skooriÜhik2 = info.get(info.size() - 1);

                if (!skoor1.equals("null")) skoor1 = "\"" + skoor1 + "\"";
                if (!skooriÜhik1.equals("null")) skooriÜhik1 = "\"" + skooriÜhik1 + "\"";
                if (!skooriÜhik2.equals("null")) skooriÜhik2 = "\"" + skooriÜhik2 + "\"";

                String JSON = String.format(
                        "{\"timestamp\":\"%s\", \"edetabel_id\":%s, \"edetabel_nimi\":\"%s\", \"osaleja\":\"%s\", \"skoor\":%s, \"skooriühik\":%s, \"skoor2\":%s, \"skooriühik2\":%s}",
                        now,
                        index + 1,
                        edetabeliNimi,
                        osalejaNimi,
                        skoor1,
                        skooriÜhik1,
                        skoor2,
                        skooriÜhik2
                );

                kirjutaja.write(JSON + "\n");
                JSONdata.add(JSON);
            }

            index++;
        }

        kirjutaja.close();
        System.out.println("Kirjutatud faili!");
        writeDataToDatabase(JSONdata, kaust);
    }
}
