package com.hp.textbreaker;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    File file, file1;
    FileOutputStream fout;
    FileInputStream fin;
    BufferedReader reader;

    int cnt = 0;

    EditText et;
    Button bTag, bFile;
    TextView tv, tvquery, tvFrom, tvTo, tvFromDate, tvToDate, tvHasAttachment, tvAttachmentType, tvAttachmentSize, tvAttachmentName, tvSubject, tvCC;

    RequestQueue requestQueue;

    String url = "http://text-processing.com/api/tag/", query, line;

    ProgressDialog dialog;

    ArrayList<String> words = new ArrayList<>();
    ArrayList<String> tags = new ArrayList<>();


    String[] ext = {"pdf", "txt", "ppt", "xml", "presentation", "doc", "document",
            "pdfs", "txts", "ppts", "xmls", "presentations", "docs", "documents",
            "text file", "text files", "videos", "video", "audio", "audios"
    };
    String[] subs = {"about", "regarding", "subject", "subjects", "on"};

    String[] date = {"jan", "feb", "mar", "apr", "may", "jun", "jul", "aug", "sep", "oct", "nov", "dec",
            "january", "february", "march", "april", "may", "june", "july", "august", "september"
            , "october", "november", "december"};
    String[] date_fr = {"from", "on", "after", "since", "by"};
    String[] date_to = {"to", "till", "before", "on"};
    String[] send = {"from", "by"};
    String[] range_ = {"days", "months", "years", "day", "month", "year", "weeks", "week"};
    String[] rem = {"the", "as", "is", "a", "an", "all", "greater", "less", "than", "more", "was"};
    String[] atta = {"attached", "attachment", "attachments"};

    String cc = "Any", att_ty = "Any", att_s = "Any", att_n = "Any", has_att = "Any", sub = "Any", fro = "Any", to = "Any", frd = "Any", tod = "Any";

    int cc_ = -1;

    ArrayList<String> exts = new ArrayList<>(Arrays.asList(ext));
    ArrayList<String> ranges = new ArrayList<>(Arrays.asList(range_));
    ArrayList<String> subss = new ArrayList<>(Arrays.asList(subs));
    ArrayList<String> dates = new ArrayList<>(Arrays.asList(date));
    ArrayList<String> date_frs = new ArrayList<>(Arrays.asList(date_fr));
    ArrayList<String> date_tos = new ArrayList<>(Arrays.asList(date_to));
    ArrayList<String> attas = new ArrayList<>(Arrays.asList(atta));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et = (EditText) findViewById(R.id.etText);
        bTag = (Button) findViewById(R.id.bTag);
        bFile = (Button) findViewById(R.id.bFile);
        tv = (TextView) findViewById(R.id.tv);
        tvFrom = (TextView) findViewById(R.id.tvFrom);
        tvTo = (TextView) findViewById(R.id.tvTo);
        tvFromDate = (TextView) findViewById(R.id.tvFromDate);
        tvToDate = (TextView) findViewById(R.id.tvToDate);
        tvHasAttachment = (TextView) findViewById(R.id.tvHasAttachment);
        tvAttachmentType = (TextView) findViewById(R.id.tvAttachmentType);
        tvAttachmentSize = (TextView) findViewById(R.id.tvAttachmentSize);
        tvAttachmentName = (TextView) findViewById(R.id.tvAttachmentName);
        tvSubject = (TextView) findViewById(R.id.tvSubject);
        tvCC = (TextView) findViewById(R.id.tvCC);
        tvquery = (TextView) findViewById(R.id.tv_query);
        requestQueue = Volley.newRequestQueue(this);
        dialog = new ProgressDialog(this);

        bFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                cc = "Any";
                att_ty = "Any";
                att_s = "Any";
                att_n = "Any";
                has_att = "Any";
                sub = "Any";
                fro = "Any";
                to = "Any"
                ;
                frd = "Any";
                tod = "Any";
                cc_ = -1;
                words.clear();
                tags.clear();

                file = new File("sdcard/Appathon/output.txt");
                file1 = new File("sdcard/Appathon/input.txt");

                try {
                    fout = new FileOutputStream(file, true);
                    fin = new FileInputStream(file1);
                    reader = new BufferedReader(new InputStreamReader(fin));

                    line = reader.readLine();
                    query = line.toLowerCase();
                    tvquery.setText(line);
                    if (!query.isEmpty())
                        tagText(query);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        bTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cc = "Any";
                att_ty = "Any";
                att_s = "Any";
                att_n = "Any";
                has_att = "Any";
                sub = "Any";
                fro = "Any";
                to = "Any"
                ;
                frd = "Any";
                tod = "Any";
                cc_ = -1;
                words.clear();
                tags.clear();
                query = et.getText().toString().toLowerCase();
                for (int j = 0; j < query.length(); j++) {
                    if (query.charAt(j) == '\'') {
                        query = query.substring(0, j) + "_s" + query.substring(j + 2, query.length());
                        break;
                    }

                }
                for (int j = 0; j < query.length(); j++) {
                    if (query.charAt(j) == '@') {
                        int k;
                        for (k = j + 1; k < query.length() && query.charAt(k) != ' '; k++) ;
                        query = query.substring(0, j) + query.substring(k, query.length());
                        break;
                    }
                }
                tvquery.setText(et.getText().toString());
                if (!query.isEmpty())
                    tagText(query);
            }
        });

    }

    private void tagText(final String st) {
        dialog.setMessage("Tagging");
        dialog.setCancelable(false);
        dialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("yooooo", response);
                        tv.setText(response);
                        dialog.dismiss();

                        String[] parts = response.split(" ");
                        for (int i = 2; i < parts.length; i++) {
                            String s = parts[i];
                            if (s.contains("/")) {
                                String[] part = s.split("/");
                                if (part[0].length() > 1 || part[0].equals("i") || Character.isDigit(part[0].charAt(0))) {
                                    words.add(part[0]);
                                    if (part[0].length()>1 && part[0].substring(0, 2).equals("cc")) {
                                        tags.add("CC");
                                        words.remove(words.size() - 1);
                                        words.add("cc");
                                    } else if (part[0].equals("citrix")) {
                                        tags.add("NN");
                                    } else {
                                        if (i != parts.length - 1)
                                            if (part[1].contains("\\"))
                                                tags.add(part[1].substring(0, part[1].length() - 2));
                                            else
                                                tags.add(part[1]);
                                        else {
                                            tags.add(part[1].substring(0, part[1].length() - 3));
                                        }
                                    }
                                }
                            }
                        }
                       /* for(int i=0; i<words.size(); i++){
                            Log.d("yoooo", words.get(i) + "    " + tags.get(i));
                        }*/
                        preprocess();
                        parseQuery();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("yooooo", error.toString());
                dialog.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("text", st);
                return params;
            }
        };
        requestQueue.add(request);
    }

    private void preprocess() {
        for (int i = 0; i < words.size(); i++) {
            boolean flag = false;
            for (int j = 0; j < rem.length; j++) {
                if (words.get(i).equals(rem[j])) {
                    words.remove(i);
                    tags.remove(i);
                    flag = true;
                    break;
                }
            }
            if (flag)
                i--;
        }
        ArrayList<String> rep = new ArrayList<>();
        for (int i = 0; i < words.size(); i++) {
            if (words.get(i).equals("byte") || words.get(i).equals("bytes")) {
                if (i>1 && words.get(i - 1).equals("mega")) {
                    words.set(i - 2, words.get(i - 2) + "mb");
                    rep.add(words.get(i - 1));
                }
                if (i>1 && words.get(i - 1).equals("kilo")) {
                    words.set(i - 2, words.get(i - 2) + "kb");
                    rep.add(words.get(i - 1));
                }
                if (i>1 && words.get(i - 1).equals("giga")) {
                    words.set(i - 2, words.get(i - 2) + "gb");
                    rep.add(words.get(i - 1));
                }
            }
        }
        for (int i = 0; i < words.size(); i++) {
            if (i>0 && words.get(i).equals("megabyte")) {
                rep.add(words.get(i));
                words.set(i - 1, words.get(i - 1) + "mb");
            }
            if (i>0 && words.get(i).equals("kilobyte")) {
                rep.add(words.get(i));
                words.set(i - 1, words.get(i - 1) + "kb");
            }
            if (i>0 && words.get(i).equals("gigabyte")) {
                rep.add(words.get(i));
                words.set(i - 1, words.get(i - 1) + "gb");
            }
        }
        for (int i = 0; i < rep.size(); i++) {
            for (int j = 0; j < words.size(); j++) {
                if (words.get(j).equals(rep.get(i))) {
                    words.remove(j);
                    tags.remove(j);
                    break;
                }
            }
        }
    }

    private void parseQuery() {
        for (int i = 0; i < words.size(); i++) {
            Log.d("yoooo", words.get(i) + "    " + tags.get(i));
        }

        //My
        if (words.contains("my"))
            to = "me";
        //Audio attachment
        if (words.contains("audio")) {
            att_ty += " mp3";
            has_att = "YES";
        }
        //Attachment Name
        if (words.contains("name"))
            att_n = words.get(words.indexOf("name") + 1) + " " + att_n;
        if (words.contains("called"))
            att_n = words.get(words.indexOf("called") + 1) + " " + att_n;
        if (words.contains("named"))
            att_n = words.get(words.indexOf("named") + 1) + " " + att_n;

        for (int i = 0; i < words.size(); i++) {
            //Sender
            if (words.get(i).equals("from") || words.get(i).equals("by")) {
                if (tags.get(i + 1).equals("NN") || tags.get(i + 1).equals("NNP") ||
                        tags.get(i + 1).equals("IN") || words.get(i+1).equals("me"))
                    fro = words.get(i + 1);
            }
            if (words.get(i).length() >= 2 && words.get(i).substring(words.get(i).length() - 2, words.get(i).length()).equals("\'s"))
                fro = words.get(i).substring(0, words.get(i).length() - 2);
            //to or cc'd to
            if (words.get(i).equals("to") && !(tags.get(i + 1).equals("CD"))) {
                if (i > 0 && !words.get(i - 1).equals("cc") && !words.get(i - 1).equals("cc\'d"))
                    to = words.get(i + 1);
                else
                    cc = words.get(i + 1);
            }
            //cc'd in
            if (i>=2 && words.get(i).equals("in") && (words.get(i - 1).equals("cc") || words.get(i - 1).equals("cc\'d")))
                cc = words.get(i - 2);

            //split word for extension
            /*String[] k = words.get(i).split(".");
            if (k.length > 1 && exts.contains(k[1])) {
                has_att = "YES";
                att_ty += "," + k[1];
                att_n = k[0] + " " + att_n;
            }*/
            //has attachment, size
            if (attas.contains(words.get(i)) || words.get(i).equals("size")) {
                if (i > 0 && tags.get(i - 1).equals("CD"))
                    att_s = words.get(i - 1);
                has_att = "YES";
                /*int j = i - 1;
                while(j>=0 && (tags.get(j).equals("NN"))){
                    att_n = words.get(j) + att_n;
                    j -= 1;
                }*/
                int j = i + 1;
                while (j < words.size()) {
                    if (tags.get(j).equals("CD"))
                        att_s = words.get(j);
                    j += 1;
                }
            }
            //Attachment type and name
            if (exts.contains(words.get(i))) {
                int j = i - 1;
                if (j >= 0) {
                    while (j >= 0 && (tags.get(j).equals("NNS") || tags.get(j).equals("NN") || tags.get(j).equals("JJ")) &&
                            !words.get(j).substring(words.get(j).length() - 2, words.get(j).length()).equals("_s")) {
                        att_n = words.get(j) + " " + att_n;
                        j -= 1;
                    }
                }
                has_att = "YES";
                att_ty += "," + words.get(i);
                if ((i + 1) < words.size()) {
                    if (tags.get(i + 1).equals("CD"))
                        att_s = words.get(i + 1);
                }
                if ((i - 1) >= 0) {
                    if (tags.get(i - 1).equals("CD"))
                        att_s = words.get(i - 1);
                }
            }
            //Subject
            if (words.get(i).equals("mails")) {
                int j = i - 1;
                if (j >= 0 && !words.get(i - 1).substring(words.get(i - 1).length() - 2, words.get(i - 1).length()).equals("_s")) {
                    String o = "";
                    while (j >= 0 && !exts.contains(words.get(j)) && !attas.contains(words.get(j))) {
                        o = words.get(j) + " " + o;
                        j -= 1;
                    }
                    sub += o;
                }
            }
            if (words.get(i).length()>1 && words.get(i).substring(words.get(i).length() - 2, words.get(i).length()).equals("_s"))
                if (!ranges.contains(words.get(i).substring(0, words.get(i).length() - 2)))
                    fro = words.get(i).substring(0, words.get(i).length() - 2);
            if (subss.contains(words.get(i))) {
                int j = i + 1;
                while (j < words.size() && (tags.get(j).equals("NNS") || tags.get(j).equals("NN") || tags.get(j).equals("JJ"))) {
                    sub += words.get(j) + " ";
                    j += 1;
                }
            }
            //Date
            if (words.get(i).equals("from") && tags.get(i + 1).equals("CD")) {
                frd = words.get(i + 1);
            }
            if (words.get(i).equals("to") && tags.get(i + 1).equals("CD"))
                tod = words.get(i + 1);
            if (words.get(i).equals("yesterday")) {
                frd = words.get(i);
                tod = words.get(i);
                if (i>0 && words.get(i - 1).equals("since"))
                    tod = "today";
            }
            if (words.get(i).equals("today")) {
                frd = words.get(i);
                tod = words.get(i);
            }
            //Date Range
            if (words.get(i).equals("last") || words.get(i).equals("past")) {
                if (tags.get(i + 1).equals("CD")) {
                    frd = "today - " + words.get(i + 1) + " " + words.get(i + 2);
                    tod = "today";
                } else {
                    frd = "today - 1 " + words.get(i + 1);
                    tod = "today";
                }
            }
            if (words.get(i).equals("ago")) {
                if (tags.get(i - 2).equals("CD")) {
                    frd = "today - " + words.get(i - 2) + ' ' + words.get(i - 1);
                    tod = frd;

                } else {
                    frd = "today - 1 " + words.get(i - 1);
                    tod = frd;
                }
            }
            if (words.get(i).equals("cc")) {
                int j = i + 1;
                cc = "";
                while (j < words.size() && tags.get(j).equals("NN")) {
                    cc += words.get(j) + " ";
                    j += 1;
                }
            }
            //received
            if (i > 0 && (words.get(i).equals("received") || words.get(i).equals("got"))&&!words.get(i-1).equals("mails"))
                to = words.get(i - 1);
            if (to.equals("i"))
                to = "me";
            //sender
            if (i > 0 && words.get(i).equals("sent") && (tags.get(i - 1).equals("NN")
                    || tags.get(i - 1).equals("IN") || tags.get(i - 1).equals("VB") || tags.get(i - 1).equals("VBP")))
                fro = words.get(i - 1);
            if (i > 0 && words.get(i).equals("sent") && (tags.get(i - 1).equals("I")))
                fro = "me";
        }

        //cc'd or cc
        if (words.contains("cc") && (cc.equals("Any") || cc.isEmpty()))
            if(words.indexOf("cc")>0)
            cc = words.get(words.indexOf("cc") - 1);//Attachment
        if (!att_ty.equals("Any"))
            att_ty = att_ty.substring(4, att_ty.length());
        if(cc.equals("i"))
            cc = "me";
        tvCC.setText(cc);
        tvHasAttachment.setText(has_att);
        tvAttachmentSize.setText(att_s);
        tvAttachmentType.setText(att_ty);
        if (!att_n.equals("Any"))
            att_n = att_n.substring(0, att_n.length() - 3);
        tvAttachmentName.setText(att_n);
        if (sub.contains("Anymy")) {
            sub = "Any";
        }
            if (!sub.equals("Any"))
                sub = sub.substring(3, sub.length());
            tvSubject.setText(sub);
            tvFrom.setText(fro);
            tvTo.setText(to);
            tvFromDate.setText(frd);
            tvToDate.setText(tod);

            String output = line + "\n" + "From: " + fro + "\n" + "To: " + to + "\n" + "From Date: " + frd + "\n" + "To Date: " + tod + "\n" +
                    "Has Attachment: " + has_att + "\n" + "Attachment Type: " + att_ty + "\n" + "Attachment Size: " + att_s + "\n" + "Attachment Name: " +
                    att_n + "\n" + "Subject: " + sub + "\n" + "CC: " + cc + "\n\n";

        try {
            fout.write(output.getBytes());

            Toast.makeText(this, ++cnt + "", Toast.LENGTH_SHORT).show();

            line = reader.readLine();

            cc = "Any"; att_ty = "Any"; att_s = "Any"; att_n = "Any"; has_att = "Any"; sub = "Any"; fro = "Any"; to = "Any"
            ;frd = "Any"; tod = "Any";
            cc_ = -1;
            words.clear();
            tags.clear();

            if(line != null && !line.isEmpty()) {
                query = line.toLowerCase();

                tvquery.setText(line);
                if (!query.isEmpty())
                    tagText(query);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        }
    }
