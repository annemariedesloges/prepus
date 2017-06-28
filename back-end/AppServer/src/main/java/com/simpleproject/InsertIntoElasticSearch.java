package com.simpleproject;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.json.JSONException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.Files;
import java.sql.*;
import java.util.HashMap;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * Created by Olivier on 2017-06-26.
 */
public class InsertIntoElasticSearch {

    private static String url = "jdbc:postgresql://s6ie1702.gel.usherbrooke.ca:5432/postgres";
    private static String user = "administrateur";
    private static String passwd = "s6infoe17";



    public static void encoder(int depot) {

            try {
                Class.forName("org.postgresql.Driver");
                Connection conn = DriverManager.getConnection(url, user, passwd);
                //Création d'un objet Statement
                Statement state = conn.createStatement();
                //Query d'insert
                String remiseQuery = "SELECT id, location, nom from itération1.document WHERE rem_id = " + depot + ";";
                String noremiseQuery = "SELECT id, location, nom from itération1.document;";
                String query = null;
                if (depot == 0) {
                    query = noremiseQuery;
                } else {
                    query = remiseQuery;
                }
                ResultSet result = state.executeQuery(query);
                // Get path and name columnid to concatenate
                ResultSetMetaData resultMeta = result.getMetaData();
                int pathID = 0;
                int nameID = 0;
                int ID = 0;
                for (int i = 1; i <= resultMeta.getColumnCount(); i++) {
                    if (resultMeta.getColumnLabel(i).equals("location")) {
                        pathID = i;
                    }
                    if (resultMeta.getColumnLabel(i).equals("nom")) {
                        nameID = i;
                    }
                    if (resultMeta.getColumnLabel(i).equals("id")) {
                        ID = i;
                    }
                }
                // Concatenate path and filename to return
                try {
                    //connecting to ElasticSearch
                    TransportClient client = new PreBuiltTransportClient(org.elasticsearch.common.settings.Settings.EMPTY)
                            .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
                    while (result.next()) {
                        //push files in elasticSearch
                        String path = GetPath.path() + result.getObject(pathID).toString() + result.getObject(nameID).toString();
                        if(new File(path).exists()) {
                            String json = "{" +
                            "\"data\":\"" + PdfTo64.encoder(GetPath.path() + result.getObject(pathID).toString() + result.getObject(nameID).toString()) + "\"" +
                            "}";
                            IndexResponse response = client.prepareIndex(Integer.toString(depot), "pdf", result.getObject(ID).toString())
                            .setSource(json).setPipeline("attachment")
                            .get();
                        }
                    }
                    client.close();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                result.close();
                state.close();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
    }
}
