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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import org.json.JSONObject;

public class Main {
    /**
     * lisab massiivi string elemendi
     *
     * @param massiiv - antud massiiv
     * @param element - antud element
     * @return - massiiv koos uue elemendiga.
     */
    public static String[] lisaMassiivi(String[] massiiv, String element) {
        String[] uus = new String[massiiv.length + 1];

        System.arraycopy(massiiv, 0, uus, 0, massiiv.length);
        uus[massiiv.length] = element;

        return uus;
    }

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
            StringBuilder päring = new StringBuilder("insert into data (edetabel_id, edetabel_nimi, osaleja, skoor, aeg) values ");

            int addedCount = 0;

            for (int i = 0; i < data.size(); i++) {
                JSONObject jsonobject = new JSONObject(data.get(i));

                if (!relevantsedEdetabelid.contains(jsonobject.getString("edetabel_nimi"))) {
                    continue;
                }

                if (addedCount != 0) {
                    päring.append(", ");
                } 

                päring.append(String.format("(%d, \"%s\", \"%s\", %.2f, \"%s\")", 
                        jsonobject.getInt("edetabel_id"), 
                        jsonobject.getString("edetabel_nimi"), 
                        jsonobject.getString("osaleja"), 
                        jsonobject.getFloat("skoor"), 
                        jsonobject.getString("timestamp")
                    )
                );
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
        HttpResponse<String> unparsedData = scrapeWebsite("http://www.phxc.ee");
        String[] bodyData = unparsedData.body().replace("\t", "").split("\n");

        String kaust;
        if (args != null && args.length > 0) {
            kaust = args[0];
        } else {
            kaust = "./";
        }


        // Massiivid osalejate ja skooride jaoks
        String[] osalejad = new String[0];
        String[] skoorid = new String[0];
        String[] edetabelid = new String[0];

        // Käime kõik read läbi ja otsime osaleja ja skoori.
        for (String line : bodyData) {
            if (line.startsWith("<h1>")) {
                edetabelid = lisaMassiivi(edetabelid, line.substring(4, line.length() - 5));
            }
            if (line.startsWith("<span class=\"pseudo\">")) {
                String lineVormistatud = line.substring(21, line.length() - 7);
                osalejad = lisaMassiivi(osalejad, lineVormistatud);
            }
            if (line.startsWith("<span class=\"skoor\">")) {
                String lineVormistatud = line.substring(20, line.length() - 7).replace("<span class=\"komakoht\">", "").replace("</span>", "");
                skoorid = lisaMassiivi(skoorid, lineVormistatud);
            }
        }

        ArrayList<String> JSONdata = new ArrayList<String>();

        // Viime JSON vormi ja lisame faili.
        int edetabel = 0;
        for (int i = 0; i < osalejad.length; i++) {
            // osaleja ja skoor
            String osaleja = osalejad[i];
            String osalejaNr = osaleja.split("\\.")[0];
            String skoor = skoorid[i];

            // timestamp
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date currentDate = new Date();
            String now = dateFormat.format(currentDate);

            // lisa faili
            if (!osalejaNr.equals("Juh")) {
                // edetabeli id.
                if (Integer.parseInt(osalejaNr) == 1) edetabel++;

                // ava fail kirjutamiseks
                File fail = new File(kaust + "data_" + now + ".txt");
                FileWriter kirjutaja = new FileWriter(fail, true);

                // vormista JSON
                String JSON = String.format(
                        "{\"timestamp\":\"%s\", \"edetabel_id\":%s, \"edetabel_nimi\":\"%s\",\"osaleja\":\"%s\", \"skoor\":%s}",
                        now,
                        edetabel,
                        edetabelid[edetabel - 1],
                        osaleja.split("\\.")[1].substring(1),
                        Float.parseFloat(skoor.substring(0, skoor.length() - 3))
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