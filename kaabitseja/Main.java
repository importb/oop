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


    /**
     * täidab edetabel, osalejad, skoorid, skooriühikud listid.
     *
     * @param unparsedData - antud lehekülje andmed
     */
    public static void leiaAndmed(HttpResponse<String> unparsedData) {
        if (unparsedData != null) {
            String[] bodyData = unparsedData.body().replace("\t", "").split("\n");


            int edetabel = 0;
            // Osaleja skoorid
            List<String> osalejaSkoorid = new ArrayList<>();
            List<String> osalejaSkooriühikud = new ArrayList<>();

            for (String line : bodyData) {
                // Edetabeli nimed
                if (line.startsWith("<h1>")) {
                    edetabelid.add(line.substring(4, line.length() - 5));
                    osalejad.add(new ArrayList<>());
                    skoorid.add(new ArrayList<>());
                    skooriühikud.add(new ArrayList<>());

                    if (edetabel > 0) {
                        skoorid.get(edetabel - 1).add(osalejaSkoorid);
                        skooriühikud.get(edetabel - 1).add(osalejaSkooriühikud);
                        osalejaSkoorid = new ArrayList<>();
                        osalejaSkooriühikud = new ArrayList<>();
                    }

                    edetabel++;
                }

                if (edetabel > 0) {
                    // Osaleja nimed
                    if (line.contains("pseudo")) {
                        skoorid.get(edetabel - 1).add(osalejaSkoorid);
                        skooriühikud.get(edetabel - 1).add(osalejaSkooriühikud);
                        osalejaSkoorid = new ArrayList<>();
                        osalejaSkooriühikud = new ArrayList<>();


                        // eemaldame juhendajad.
                        if (line.substring(21, line.length() - 7).split("\\.")[0].equals("Juh")) continue;

                        String osalejaNimi = line.substring(21, line.length() - 7).split("\\.")[1];

                        osalejad.get(edetabel - 1).add(osalejaNimi.trim());
                    }

                    if (line.contains("skoor\"")) {
                        String[] skooriAndmed = line.substring(20, line.length() - 7)
                                .replace("<span class=\"komakoht\">", "")
                                .replace("</span>", "").split(" ");

                        // skoor
                        osalejaSkoorid.add(skooriAndmed[0]);

                        // skooriühik
                        if (skooriAndmed.length > 1) {
                            osalejaSkooriühikud.add(skooriAndmed[1]);
                        } else {
                            osalejaSkooriühikud.add("");
                        }
                    }
                }
            }

            // Viimane osaleja ka.
            skoorid.get(edetabel - 1).add(osalejaSkoorid);
            skooriühikud.get(edetabel - 1).add(osalejaSkooriühikud);

            // Clean up
            for (List<List<String>> s : skoorid) {
                s.removeIf(List::isEmpty);
            }
            for (List<List<String>> sü : skooriühikud) {
                sü.removeIf(List::isEmpty);
            }
        }
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
                    päring.append(String.format("(%d, \"%s\", \"%s\", %.2f, NULL, \"%s\")",
                        jsonobject.getInt("edetabel_id"),
                        jsonobject.getString("edetabel_nimi"),
                        jsonobject.getString("osaleja"),
                        jsonobject.getFloat("skoor"),
                        jsonobject.getString("timestamp")
                    ));
                } else {
                    päring.append(String.format("(%d, \"%s\", \"%s\", %.2f, %.2f, \"%s\")",
                    jsonobject.getInt("edetabel_id"),
                    jsonobject.getString("edetabel_nimi"),
                    jsonobject.getString("osaleja"),
                    jsonobject.getFloat("skoor"),
                    jsonobject.getFloat("skoor2"),
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
        leiaAndmed(unparsedData);

        String kaust;
        if (args != null && args.length > 0) {
            kaust = args[0];
        } else {
            kaust = "./";
        }

        ArrayList<String> JSONdata = new ArrayList<String>();

        // timestamp
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date currentDate = new Date();
        String now = dateFormat.format(currentDate);

        // Koostame faili
        for (int i = 0; i < osalejad.size(); i++) {
            String edetabeliNimi = edetabelid.get(i);

            for (int j = 0; j < osalejad.get(i).size(); j++) {
                // ava fail kirjutamiseks
                File fail = new File(kaust + "data_" + now + ".txt");
                FileWriter kirjutaja = new FileWriter(fail, true);

                // osaleja andmed
                String osalejaNimi = osalejad.get(i).get(j);
                List<String> osalejaSkoorid = skoorid.get(i).get(j);
                List<String> osalejaSkooriühikud = skooriühikud.get(i).get(j);

                // skoor 1
                Float skoor1 = Float.valueOf(osalejaSkoorid.get(0));
                String skooriÜhik1 = osalejaSkooriühikud.get(0);

                if (skooriÜhik1.isEmpty()) skooriÜhik1 = "NULL";
                else skooriÜhik1 = "\"" + skooriÜhik1 + "\"";

                // skoor 2
                String skoor2 = "NULL";
                String skooriÜhik2 = "NULL";

                if (osalejaSkoorid.size() > 1) {
                    skoor2 = String.valueOf(Float.valueOf(osalejaSkoorid.get(1)));
                    skooriÜhik2 = "\"" + osalejaSkooriühikud.get(1) + "\"";
                }

                // ava fail kirjutamiseks
                String JSON = String.format(
                        "{\"timestamp\":\"%s\", \"edetabel_id\":%s, \"edetabel_nimi\":\"%s\", \"osaleja\":\"%s\", \"skoor\":%s, \"skooriühik\":%s, \"skoor2\":%s, \"skooriühik2\":%s}",
                        now,
                        i+1,
                        edetabeliNimi,
                        osalejaNimi,
                        skoor1,
                        skooriÜhik1,
                        skoor2,
                        skooriÜhik2
                );

                JSONdata.add(JSON);

                // kirjuta ja sulge.
                kirjutaja.write(JSON + "\n");
                kirjutaja.close();
            }
        }

        System.out.println("Kirjutatud faili!");
        writeDataToDatabase(JSONdata, kaust);
    }
}
