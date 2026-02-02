package com.example.meetpoint;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import androidx.core.content.FileProvider;
import com.example.meetpoint.models.VisitorModel;
import com.google.firebase.firestore.FirebaseFirestore;
import java.io.File;
import java.io.FileWriter;

public class CsvExporter {
    public static void exportVisitor(Context context, String visitorId) {

        FirebaseFirestore.getInstance()
                .collection("Visitors")
                .document(visitorId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) return;

                    VisitorModel visitor = doc.toObject(VisitorModel.class);
                    if (visitor == null) return;

                    visitor.setId(doc.getId());
                    exportVisitor(context, visitor); // call main method
                });
    }

    public static void exportVisitor(Context context, VisitorModel v) {
        try {
            File dir = new File(
                    context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),
                    "Visitors"
            );
            if (!dir.exists()) dir.mkdirs();

            File file = new File(dir, "visitor_" + v.getId() + ".csv");

            FileWriter writer = new FileWriter(file);
            writer.append("Token,Name,Phone,Email,Gender,Purpose,WhomToMeet,VisitDate,VisitTime,Status\n");

            writer.append(safe(v.getToken()) + ",");
            writer.append(safe(v.getName()) + ",");
            writer.append(safe(v.getPhone()) + ",");
            writer.append(safe(v.getEmail()) + ",");
            writer.append(safe(v.getGender()) + ",");
            writer.append(safe(v.getPurpose()) + ",");
            writer.append(safe(v.getWhomToMeet()) + ",");
            writer.append(safe(v.getVisitDate()) + ",");
            writer.append(safe(v.getVisitTime()) + ",");
            writer.append(safe(v.getStatus()) + "\n");

            writer.flush();
            writer.close();

            Uri uri = FileProvider.getUriForFile(
                    context,
                    context.getPackageName() + ".provider",
                    file
            );

            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setDataAndType(uri, "text/csv");
            i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(i);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String safe(String s) {
        return s == null ? "" : s.replace(",", " ");
    }
}